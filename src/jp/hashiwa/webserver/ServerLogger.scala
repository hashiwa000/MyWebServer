package jp.hashiwa.webserver

/**
 * Created by Hashiwa on 2015/05/18.
 */
object ServerLogger {
  val debug = System.getProperty("jp.hashiwa.webserver.debug", "false").toLowerCase == "true"
  val prefix = "** "

  def println(): Unit = {
    if (debug) println(prefix)
  }

  def print(s: String): Unit = {
    if (debug) print(prefix + s)
  }

  def println(s: String): Unit = {
    if (debug) println(prefix + s)
  }

  def println(list: List[String]): Unit = {
    if (debug) list.foreach(s => println(prefix + s))
  }

  def isDebug() = debug
}
