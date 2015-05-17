package jp.hashiwa.webserver.sample

import jp.hashiwa.webserver.{WebApp, HttpRequest, HttpResponse}

import scala.collection.mutable.Map

/**
 * Created by Hashiwa on 2015/05/13.
 */
class RESTfullApp extends WebApp {
  val uriPrefix = "/rest/"
  val db: Map[Int, String] = Map(1 -> "Tanaka", 2-> "Suzuki")

  override def doGet(request: HttpRequest): HttpResponse = {
    val uri = getURI(request)

    // if uri is not number, throw NumberFormatException
    val key = uri.toInt

    db.get(key) match {
      case Some(value) =>
        new HttpResponse(HttpResponse.OK_CODE, List(value))
      case None =>
        new HttpResponse(404, List(key + " is not found"))
    }
  }

//  override def doPost(request: HttpRequest): HttpResponse = {
//  }

  private def getURI(request: HttpRequest): String = {
    val uri = request.uri

    if (!uri.startsWith(uriPrefix))
      throw new Exception("Illegal URI")

    uri.substring(uriPrefix.length)
  }

}
