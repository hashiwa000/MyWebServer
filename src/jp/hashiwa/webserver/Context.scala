package jp.hashiwa.webserver

import java.io.IOException

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
class Context(_rootdir: String) {
  val rootDir = _rootdir
  val rootingMap = parseRooting()

  private def parseRooting(): Map[String, String] = {
    val rooting = rootDir + "/rootings.txt"
    val source = Source.fromFile(rooting)
    source.getLines()
      .filter(line => !(line.trim.startsWith("#")))  // ignore comment
      .map(line => {
        val elems = line.split("  *")
        if (elems.length != 2) throw new IOException("Parse Error : " + rooting)
        elems(0) -> elems(1)
      })
      .toMap
  }
}
