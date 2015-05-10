package jp.hashiwa.webserver

import java.io.{File, IOException}
import java.net.{URL, URLClassLoader}

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
class Context(_rootdir: String) {
  val rootDir = _rootdir
  val rootingMap = parseRooting()
  val classLoader = getClassLoader()

  def loadClass(name: String): Class[_] = classLoader.loadClass(name)

  def resolve(originalUri: String): String = {
    rootingMap.get(originalUri) match {
      case Some(value) => value
      case None => originalUri
    }
  }

  private def parseRooting(): Map[String, String] = {
    val rooting = rootDir + "/rootings.txt"
    val source = Source.fromFile(rooting)
    source.getLines()
      .filter(_.trim.startsWith("#") == false)  // ignore comments
      .filter(_.trim != "")  // ignore empty lines
      .map(line => {
        val elems = line.split("  *")
        if (elems.length != 2)
          throw new IOException("Parse Error : " + rooting)
        elems(0) -> elems(1)
      })
      .toMap
  }

  private def getClassLoader(): ClassLoader = {
    val sp = File.pathSeparatorChar
    val url = new URL(
      "file://" + rootDir + sp + "app" + sp + "classes")

    val parent = getClass().getClassLoader()
    new URLClassLoader(Array[URL](url), parent)
  }
}
