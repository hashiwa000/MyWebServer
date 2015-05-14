package jp.hashiwa.webserver.sample

import java.util.Date

import jp.hashiwa.webserver.{WebApp, HttpResponse, HttpRequest}

/**
 * Created by Hashiwa on 2015/05/10.
 */
class WebAppImpl extends WebApp {
  override def doGet(request: HttpRequest): HttpResponse = {
    val body = List (
      "<!DOCTYPE html>" +
      "<html>" +
      "<head lang=\"en\">" +
      "<meta charset=\"UTF-8\">" +
      "<title>Sample WebApp Page</title>" +
      "</head>" +
      "<body>" +
      "<h1>Sample WebApp Page</h1>" +
      "<h2>" + request.uri + " is required by GET methods.</h2>" +
      "<h2>Date: " + new Date() + "</h2>" +
      "</body>" +
      "</html>")
    new HttpResponse(HttpResponse.OK_CODE,
      HttpResponse.OK_REASON, body)
  }

  override def doPost(request: HttpRequest): HttpResponse = {
    val body = List (
      "<!DOCTYPE html>" +
        "<html>" +
        "<head lang=\"en\">" +
        "<meta charset=\"UTF-8\">" +
        "<title>Sample WebApp Page</title>" +
        "</head>" +
        "<body>" +
        "<h1>Sample WebApp Page</h1>" +
        "<h2>" + request.uri + " is required by GET methods.</h2>" +
        "<h2>Date: " + new Date() + "</h2>" +
        "body : </br>" +
        "<p>" +
        request.body.fold("")((z, n) => z + n + "</br>") +
        "</p>" +
        "</body>" +
        "</html>")
    new HttpResponse(HttpResponse.OK_CODE,
      HttpResponse.OK_REASON, body)
  }
}
