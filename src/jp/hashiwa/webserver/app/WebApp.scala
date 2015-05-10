package jp.hashiwa.webserver.app

import jp.hashiwa.webserver.HttpRequest

/**
 * Created by Hashiwa on 2015/05/10.
 */
abstract class WebApp {
  def doGet(request: HttpRequest): List[String] =
    throw new UnsupportedOperationException("GET method is not supported.")

  def doPost(request: HttpRequest): List[String] =
    throw new UnsupportedOperationException("POST method is not supported.")

  def doPut(request: HttpRequest): List[String] =
    throw new UnsupportedOperationException("PUT method is not supported.")

  def doDelete(request: HttpRequest): List[String] =
    throw new UnsupportedOperationException("DELETE method is not supported.")
}
