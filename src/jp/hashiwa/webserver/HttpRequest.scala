package jp.hashiwa.webserver

import java.io.{InputStreamReader, BufferedReader, InputStream}

import jp.hashiwa.webserver.exception.BadRequestException

/**
 * Created by Hashiwa on 2015/05/09.
 */
case class HttpRequest(method: String, uri: String, version: String, headers: Map[String,String], body: List[String]) {
}

object HttpRequest {
  val ignoreBodyMethod = List("GET")  // FIXME

  def parse(in: InputStream): HttpRequest = {
    val br = new BufferedReader(new InputStreamReader(in))
    val firstLine = br.readLine();
    if (firstLine == null) throw new BadRequestException("No contents in HTTP Request.")

    val elems: Array[String] = firstLine split " "
    if (elems.length != 3)  throw new BadRequestException("Invalid first line : " + firstLine)

    val method = elems(0)
    val requestUrl = elems(1)
    val httpVersion = elems(2)

    val headers = Iterator
      .continually(br.readLine())
      .takeWhile(line => line != null && line != "")
//      .map(s => {println(s); s})  // for debug
      .map(s => {
        val key = s.substring(0, s.indexOf(':'))
        val value = s.substring(s.indexOf(':')+1)
        key -> value
      }).toMap

    println("** start body")

    val body = if (ignoreBody(method)) List() else
      Iterator
      .continually(br.readLine())
      .takeWhile(line => line != null && line != "")
//      .map(s => {println(s); s})  // for debug
      .toList

    HttpRequest(method, requestUrl, httpVersion, headers, body)
  }

  private def ignoreBody(method: String): Boolean =
    ignoreBodyMethod contains method.toUpperCase()
}