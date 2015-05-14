package jp.hashiwa.webserver

import java.io._
import java.net.{InetSocketAddress, ServerSocket, Socket}

/**
 * Created by Hashiwa on 2015/05/09.
 */

object Main {
  val DEFAULT_PORT = 80
  val DEFAULT_ROOTDIR = "rootdir/"

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
    println("** start server")

    val serverSocket = new ServerSocket()
    serverSocket.bind(addr)
    println("** bind at " + addr)
    println("** root directory is " + context.rootDir)

    Iterator
      .continually(serverSocket.accept())
      .foreach(s => {
        println("** accept " + s)
        new Thread() {
          override def run(): Unit = processOneRequest(s, context)
        }.start()
      })
  }

  def processOneRequest(socket: Socket, context: Context) = {

    println("** read from " + socket)
    val request = HttpRequest.parse(socket.getInputStream)

    val response = HttpResponse.getResponse(request, context)

    println("** write to " + socket)
    response.writeTo(socket.getOutputStream)

    socket.close()
  }

}
