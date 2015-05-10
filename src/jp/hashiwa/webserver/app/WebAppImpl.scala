package jp.hashiwa.webserver.app

import java.util.Date

import jp.hashiwa.webserver.HttpRequest

/**
 * Created by Hashiwa on 2015/05/10.
 */
class WebAppImpl extends WebApp {
  override def doGet(request: HttpRequest): List[String] = {

    List (
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
  }
}
