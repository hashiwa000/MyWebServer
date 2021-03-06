package jp.hashiwa.webserver

import java.io._

import jp.hashiwa.webserver.exception.{HttpServerException, MethodNotAllowedException}

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
case class HttpResponse(code: Int, headers: Map[String, String], body: List[String]) {
  val reason = HttpResponse.reasonPhraseOf(code)

  def this(code: Int, body: List[String]) =
    this(code, Map[String, String](), body)

  def writeTo(out: OutputStream): Unit = {
    val writer = new BufferedWriter(new OutputStreamWriter(out))
    writer.write(HttpResponse.VERSION + " " + code + " " + reason)
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
  val VERSION = "HTTP/1.1"
  val OK_CODE = 200

  def getResponse(request: HttpRequest, context: Context): HttpResponse = {
    val headers = Map[String, String]()
    val uri = context.resolve(request.uri)
    val rootDir = context.rootDir

    getBodyFromFile(rootDir, uri, context) match {
      case Some(body) => HttpResponse(200, headers, body)
      case None =>
        getBodyFromClass(uri, request, context) match {
          case Some(res) => res
          case None      => getError(404)
        }
    }
  }

  def getError(code: Int): HttpResponse = {
    val message = reasonPhraseOf(code)
    val headers = Map[String, String]()
    val body = List[String] (
      "<html>" +
        "<head>" +
        "<title>Error: Not Found</title>" +
        "</head>" +
        "<body>" +
        "<h1>" + code + ": " + message + "</h1>" +
        "</body>" +
        "</html>")
    HttpResponse(404, headers, body)
  }

  /**
   * ウェブページを取得する。
   * @param rootDir アプリケーションのルートディレクトリ
   * @param uri URI
   * @param context コンテクスト
   * @return 取得されたウェブページのbody部。取得できなかった場合はNone。
   */
  private def getBodyFromFile(rootDir: String, uri: String,
                      context: Context): Option[List[String]] = {

    val file = new File(rootDir + "web/" + uri)
    if (!file.exists()) return None

    ServerLogger.println("get body from " + file)

    val source = Source.fromFile(file)
    Some(source.getLines().toList)
  }

  /**
   * ウェブページを動的に生成する。
   * @param className ウェブページ生成に使用するクラス
   * @param request リクエスト
   * @param context コンテクスト
   * @return 生成されたウェブページのbody部。アプリケーションが見つからない場合はNone。
   */
  private def getBodyFromClass(className: String, request: HttpRequest,
                           context: Context): Option[HttpResponse] = {
    val clazz = context.loadClass(className) match {
      case Some(c) => c
      case None => return None
    }

    val obj = clazz.newInstance()
    if (!obj.isInstanceOf[WebApp]) return None

    val app = obj.asInstanceOf[WebApp]

    ServerLogger.println("get body from " + className)

    try {
      request.method match {
        case "GET" => Some(app.doGet(request))
        case "POST" => Some(app.doPost(request))
        case "PUT" => Some(app.doPut(request))
        case "DELETE" => Some(app.doDelete(request))
        case _ => Some(new HttpResponse(405, List(request.method + " is not acceptable.")))
      }
    } catch {
      case e: MethodNotAllowedException =>
        Some(new HttpResponse(405, List(request.method + " is not acceptable in this application.")))
      case e: HttpServerException =>
        Some(new HttpResponse(e.code, List(e.getLocalizedMessage)))
      case e: Exception => {
        ServerLogger.println(e.getLocalizedMessage)
        Some(new HttpResponse(500, List("Internal Server Error")))
      }
    }
  }

  private def reasonPhraseOf(code: Int): String =
    code match {
      case 100 => "Continue"
      case 101 => "Switching Protocols"
      case 200 => "OK"
      case 201 => "Created"
      case 202 => "Accepted"
      case 203 => "Non-Authoritative Information"
      case 204 => "No Content"
      case 205 => "Reset Content"
      case 206 => "Partial Content"
      case 300 => "Multiple Choices"
      case 301 => "Moved Permanently"
      case 302 => "Found"
      case 303 => "See Other"
      case 304 => "Not Modified"
      case 305 => "Use Proxy"
      case 307 => "Temporary Redirect"
      case 400 => "Bad Request"
      case 401 => "Unauthorized"
      case 402 => "Payment Required"
      case 403 => "Forbidden"
      case 404 => "Not Found"
      case 405 => "Method Not Allowed"
      case 406 => "Not Acceptable"
      case 407 => "Proxy Authentication Required"
      case 408 => "Request Time-out"
      case 409 => "Conflict"
      case 410 => "Gone"
      case 411 => "Length Required"
      case 412 => "Precondition Failed"
      case 413 => "Request Entity Too Large"
      case 414 => "Request-URI Too Large"
      case 415 => "Unsupported Media Type"
      case 416 => "Requested range not satisfiable"
      case 417 => "Expectation Failed"
      case 500 => "Internal Server Error"
      case 501 => "Not Implemented"
      case 502 => "Bad Gateway"
      case 503 => "Service Unavailable"
      case 504 => "Gateway Time-out"
      case 505 => "HTTP Version not supported"
      case _   => "Unknown code"
    }
}