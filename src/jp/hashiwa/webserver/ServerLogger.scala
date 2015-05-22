package jp.hashiwa.webserver

/**
 * Created by Hashiwa on 2015/05/18.
 */
object ServerLogger {
  val debug = System.getProperty("jp.hashiwa.webserver.debug", "false").toLowerCase == "true"
  // val debug = true;
  val prefix = "** "

  def println(): Unit = {
    if (debug) this.println("")
  }

  def print(s: String): Unit = {
    if (debug) System.out.print(prefix + s)
  }

  def println(s: String): Unit = {
    if (debug) System.out.println(prefix + s)
  }

  def println(list: List[String]): Unit = {
    if (debug) list.foreach(s => this.println(s))
  }

  def isDebug() = debug
}
