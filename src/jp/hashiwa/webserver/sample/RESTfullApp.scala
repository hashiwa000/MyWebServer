package jp.hashiwa.webserver.sample

import jp.hashiwa.webserver.exception.BadRequestException
import jp.hashiwa.webserver.{WebApp, HttpRequest, HttpResponse}

import scala.collection.mutable.Map

/**
 * Created by Hashiwa on 2015/05/13.
 */
class RESTfullApp extends WebApp {
  val uriPrefix = "/rest/"
  val db = RESTfullAppDB.getDB

  /**
   * READ
   * @param request
   * @return
   */
  override def doGet(request: HttpRequest): HttpResponse = {
    val uri = getURI(request)

    val key = getKeyOf(uri)

    db.get(key) match {
      case Some(value) =>
        new HttpResponse(HttpResponse.OK_CODE, List(value))
      case None =>
        new HttpResponse(404, List(key + " is not found"))
    }
  }

  /**
   * CREATE
   * @param request
   * @return
   */
  override def doPost(request: HttpRequest): HttpResponse = {
    val uri = getURI(request)

    val key = getKeyOf(uri)
    val value = request.body.foldLeft("")((acc, x) => acc + x)

    db.get(key) match {
      case Some(value) =>
        new HttpResponse(409, List(key + " already exists.")) // Conflict
      case None => {
        db.put(key, value)
        new HttpResponse(201, List(value + " is created."))  // Created
      }
    }
  }

  /**
   * UPDATE
   * @param request
   * @return
   */
  override def doPut(request: HttpRequest): HttpResponse = {
    val uri = getURI(request)

    val key = getKeyOf(uri)
    val value = request.body.foldLeft("")((acc, x) => acc + x)

    db.get(key) match {
      case Some(oldValue) => {
        db.put(key, value)
        new HttpResponse(HttpResponse.OK_CODE, List(oldValue + " is updated to " + value))
      }
      case None => new HttpResponse(404, List(key + " is not found"))
    }
  }

  /**
   * DELETE
   * @param request
   * @return
   */
  override def doDelete(request: HttpRequest): HttpResponse = {
    val uri = getURI(request)

    val key = getKeyOf(uri)

    db.remove(key) match {
      case Some(x) => new HttpResponse(HttpResponse.OK_CODE, List())
      case None    => new HttpResponse(204, List(key + " is not found"))
    }
  }

  private def getURI(request: HttpRequest): String = {
    val uri = request.uri

    if (!uri.startsWith(uriPrefix))
      throw new Exception("Illegal URI")

    uri.substring(uriPrefix.length)
  }

  private def getKeyOf(uri: String): Int =
    try {
      // if uri is not number, throw NumberFormatException
      uri.toInt
    } catch {
      case e: NumberFormatException =>
        throw new BadRequestException(uri + " is invalid URI")
    }
}

object RESTfullAppDB {
  private val map: Map[Int, String] = Map(1 -> "Tanaka", 2-> "Suzuki")
  def getDB = map
}