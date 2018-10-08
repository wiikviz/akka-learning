package ru.sber.cb.ap.gusli.actor.projects.read.util

import java.nio.file.Path

object FileContentReader {
  def readFileContent(path: Path): String = {
    val source = scala.io.Source.fromFile(path.toFile)
    val fileContent = try source.mkString
    finally source.close()
    fileContent
  }
}
