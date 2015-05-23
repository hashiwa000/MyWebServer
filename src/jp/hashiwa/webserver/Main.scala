package jp.hashiwa.webserver

import java.io._
import java.net.{InetSocketAddress, ServerSocket, Socket}
import java.util.concurrent.Executors

import jp.hashiwa.webserver.exception.BadRequestException

/**
 * Created by Hashiwa on 2015/05/09.
 */

object Main {
  val DEFAULT_PORT = 80
  val DEFAULT_ROOTDIR = "rootdir/"
  val pool = Executors.newCachedThreadPool()
  val warmupThreadNum = System.getProperty("jp.hashiwa.webserver.warmup", "5").toInt

  def main(args: Array[String]): Unit = {
    val (addr, context) = args.toList match {
      case Nil => (new InetSocketAddress(DEFAULT_PORT), new Context(DEFAULT_ROOTDIR))
      case port :: Nil => (new InetSocketAddress(port.toInt), new Context(DEFAULT_ROOTDIR))
      case port :: dir :: Nil => (new InetSocketAddress(port.toInt), new Context(dir))
      case _ => throw new IllegalArgumentException("Arguments are [port] [rootdir]")
    }

    startServer(addr, context)
  }

  def startServer(addr: InetSocketAddress, context: Context): Unit = {

    val serverSocket = new ServerSocket()
    serverSocket.bind(addr)

    if (0 < warmupThreadNum) {
      new Thread() {
        override def run = warmupServer(addr)
      }.start
    }

    ServerLogger.println("bind at " + addr)
    ServerLogger.println("root directory is " + context.rootDir)
    println("Server Ready.")

    Iterator
      .continually(serverSocket.accept())
      .foreach(s => {
        ServerLogger.println("accept " + s)
        pool.execute(new Runnable() {
          override def run(): Unit = processOneRequest(s, context)
        })
      })
  }

  def processOneRequest(socket: Socket, context: Context) = {
    try {
      ServerLogger.println("read from " + socket)
      val request = HttpRequest.parse(socket.getInputStream)

      val response = HttpResponse.getResponse(request, context)

      ServerLogger.println("write to " + socket)
      response.writeTo(socket.getOutputStream)

    } catch {
      case e: BadRequestException => {
        ServerLogger.println("Exception : " + e.getLocalizedMessage)
        val response = HttpResponse.getError(400)
        response.writeTo(socket.getOutputStream)
      }
    } finally {
      if (socket != null) socket.close()
    }
  }

  private def warmupServer(addr: InetSocketAddress): Unit = {
    println("Warm Up Server : Start (" + warmupThreadNum + ")")

    val threads = (0 until warmupThreadNum).map(x => new Thread() {
      override def run(): Unit = {
        val s = new Socket("localhost", addr.getPort)
        s.getOutputStream.write("GET / HTTP/1.1\r\n\r\n".getBytes())
        Iterator.continually(s.getInputStream.read)
                .takeWhile(b => b>=0)
//                .foreach(print)  // for debug
      }
    })

    threads.foreach(_.start)
    threads.foreach(_.join)

    println("Warm Up Server : Done")
  }
}
