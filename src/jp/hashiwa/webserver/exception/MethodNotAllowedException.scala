package jp.hashiwa.webserver.exception

/**
 * Created by Hashiwa on 2015/05/25.
 */
class MethodNotAllowedException(msg: String) extends HttpServerException(405, msg) {}
