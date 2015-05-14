package jp.hashiwa.webserver

import java.io.{InputStreamReader, BufferedReader, InputStream}

/**
 * Created by Hashiwa on 2015/05/09.
 */
case class HttpRequest(method: String, uri: String, version: String, headers: Map[String,String], body: List[String]) {
}

object HttpRequest {
  def parse(in: InputStream): HttpRequest = {
    val br = new BufferedReader(new InputStreamReader(in))
    val firstLine = br.readLine();
    if (firstLine == null) return null;

    val elems: Array[String] = firstLine split " "
    if (elems.length != 3) return null;

    val headers = Iterator
      .continually(br.readLine())
      .takeWhile(line => line != null && line != "")
      .map(s => {
        val key = s.substring(0, s.indexOf(':'))
        val value = s.substring(s.indexOf(':')+1)
        key -> value
      }).toMap

    val body = Iterator
      .continually(br.readLine())
      .takeWhile(line => line != null && line != "")
      .toList

    HttpRequest(elems(0), elems(1), elems(2), headers, body)
  }
}