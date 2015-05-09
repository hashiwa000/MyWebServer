package jp.hashiwa.webserver

import java.io._

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
case class HttpResponse(code: Int, reason: String, headers: Map[String, String], body: List[String]) {
  val VERSION = "HTTP/1.1"

  def writeTo(out: OutputStream): Unit = {
    val writer = new BufferedWriter(new OutputStreamWriter(out))
    writer.write(VERSION + " " + code + " " + reason)
    writer.write("\r\n")

    headers
      .map(e => e._1 + ": " + e._2 + "\r\n")
      .foreach(writer.write)

    writer.write("\r\n")

    body
      .map(s => s + "\r\n")
      .foreach(writer.write)

    writer.flush()
    writer.close()
  }
}

object HttpResponse {
  def doGet(request: HttpRequest, context: Context): HttpResponse = {
    val headers = Map[String, String]()

    val file = getFile(request, context) match {
      case Some(f) => f
      case None => return doError404(request)
    }

    val source = Source.fromFile(file)
    val body = source.getLines().toList

    HttpResponse(200, "OK", headers, body)
  }

  def doError404(request: HttpRequest): HttpResponse = {
    val headers = Map[String, String]()
    val body = List[String] (
      "<html>" +
        "<head>" +
        "<title>Error: Not Found</title>" +
        "</head>" +
        "<body>" +
        "<h1>404: Not found</h1>" +
        request.uri + " is not found." +
        "</body>" +
        "</html>")
    HttpResponse(404, "Not Found", headers, body)
  }

  private def getFile(request: HttpRequest, context: Context): Option[File] = {
    val rootDir = context.rootDir
    val map = context.rootingMap
    val originalUri = request.uri

    val uri = map.get(originalUri) match {
      case Some(value) => value
      case None => originalUri
    }

    val file = new File(rootDir + "web/" + uri)
    if (!file.exists()) return None
    Some(file)
  }
}