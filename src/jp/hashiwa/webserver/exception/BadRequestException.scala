package jp.hashiwa.webserver.exception

/**
 * Created by Hashiwa on 2015/05/16.
 */
class BadRequestException(msg: String) extends HttpServerException(400, msg) {}
