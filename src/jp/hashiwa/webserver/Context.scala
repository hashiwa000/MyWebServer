package jp.hashiwa.webserver

import java.io.{FilenameFilter, File, IOException}
import java.net.{URL, URLClassLoader}

import scala.io.Source

/**
 * Created by Hashiwa on 2015/05/09.
 */
class Context(_rootdir: String) {
  val rootDir = _rootdir
  val rootingMap = parseRooting()
  val classLoader = getClassLoader()

  def loadClass(name: String): Option[Class[_]] =
    try {
      Some(classLoader.loadClass(name))
    } catch {
      case e: ClassNotFoundException => {
        println("** ClassNotFoundException : " + e.getLocalizedMessage)
        None
      }
    }

  def resolve(originalUri: String): String =
//    rootingMap.get(originalUri) match {
//      case Some(value) => value
//      case None => originalUri
//    }
    rootingMap.filter(e => originalUri.matches(e._1))
              .headOption
              .map(_._2) match {
      case Some(url) => url
      case None      => originalUri
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
    val classesDir = toURL(rootDir + "/app/classes/")
    val libs = toURLs(rootDir + "/app/lib/")

    val urls = List(classesDir) ++: libs

    print("** classpath is ")
    urls.foreach(u => print(u + ", "))
    println()

    val parent = getClass().getClassLoader()
    new URLClassLoader(urls, parent)
  }

  private def toURL(filePath: String) =
    new File(filePath).toURI().toURL()

  private def toURLs(fileDir: String): Array[URL] = {
    val libs = new File(fileDir).listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean =
          name.endsWith(".jar") || name.endsWith(".war")
    })

    if (libs == null) {
      println("** Failed to read " + fileDir)
      return Array()
    }

    libs.map(_.toURI().toURL())
  }

}
