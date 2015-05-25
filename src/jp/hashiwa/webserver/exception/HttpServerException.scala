package jp.hashiwa.webserver.exception

/**
 * Created by Hashiwa on 2015/05/25.
 */
case class HttpServerException(code: Int, msg: String) extends Exception(msg) {}
