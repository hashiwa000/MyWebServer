package jp.hashiwa.webserver

/**
 * WebApp base class.
 * One of the methods is called from web server when the page is requested.
 * Each methods return "body" of HTTP response.
 * Each methods can throw java.lang.Exception.
 * Created by Hashiwa on 2015/05/10.
 */
abstract class WebApp {
  def doGet(request: HttpRequest): HttpResponse =
    throw new UnsupportedOperationException("GET method is not supported.")

  def doPost(request: HttpRequest): HttpResponse =
    throw new UnsupportedOperationException("POST method is not supported.")

  def doPut(request: HttpRequest): HttpResponse =
    throw new UnsupportedOperationException("PUT method is not supported.")

  def doDelete(request: HttpRequest): HttpResponse =
    throw new UnsupportedOperationException("DELETE method is not supported.")
}
