package jp.hashiwa.webserver

import java.io._

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
case class HttpResponse(code: Int, reason: String, headers: Map[String, String], body: List[String]) {
  def this(code: Int, reason: String, body: List[String]) =
    this(code, reason, Map[String, String](), body)

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
  val OK_REASON = "OK"

  def getResponse(request: HttpRequest, context: Context): HttpResponse = {
    val headers = Map[String, String]()
    val uri = context.resolve(request.uri)
    val rootDir = context.rootDir

    getBodyFromFile(rootDir, uri, context) match {
      case Some(body) => HttpResponse(200, "OK", headers, body)
      case None =>
        getBodyFromClass(uri, request, context) match {
          case Some(res) => res
          case None       => getError404(request)
        }
    }
  }

  def getError404(request: HttpRequest): HttpResponse = {
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

    println("** get body from " + file)

    val source = Source.fromFile(file)
    Some(source.getLines().toList)
  }

  /**
   * ウェブページを動的に生成する。
   * @param className ウェブページ生成に使用するクラス
   * @param request リクエスト
   * @param context コンテクスト
   * @return 生成されたウェブページのbody部。生成できなかった場合はNone。
   */
  private def getBodyFromClass(className: String, request: HttpRequest,
                           context: Context): Option[HttpResponse] = {
//    println("*** " + uri)
    val clazz = context.loadClass(className) match {
      case Some(c) => c
      case None => return None
    }

    val obj = clazz.newInstance()
    if (!obj.isInstanceOf[WebApp]) return None

    val app = obj.asInstanceOf[WebApp]

    println("** get body from " + className)

    try {
      request.method match {
        case "GET" => Some(app.doGet(request))
        case "POST" => Some(app.doPost(request))
        case "PUT" => Some(app.doPut(request))
        case "DELETE" => Some(app.doDelete(request))
        case _ => None
      }
    } catch {
      case e: Exception => {
        println("** " + e.getLocalizedMessage)
        None
      }
    }
  }
}