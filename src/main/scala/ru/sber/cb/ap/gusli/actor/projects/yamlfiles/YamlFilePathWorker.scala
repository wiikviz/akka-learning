package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

object YamlFilePathWorker {
  
  def getParentIdFromPath(path : Path): Option[Long] = {
    val fileName = path.getFileName.toString.toLowerCase
    val parentName = path.getParent.getFileName.toString.toLowerCase
    if (fileName == "entity")
      None
    else if (parentName == "entity")
      Some(0L)
    else {
      val (id, name) = this.parseIdAndNameFrom(parentName)
      Some(id)
    }
  }
  
  def isYaml(path: Path): Boolean = {
    path.toFile.isFile && path.getFileName.toString.endsWith(".yaml")
  }
  
  def parseIdAndNameFromYaml(fileName: String): (Long, String) = {
    //Use dropRight(5) for cut ".yaml" in the end and don't touch in the middle if exists
    parseIdAndNameFrom(fileName.dropRight(5))
  }
  
  def parseIdAndNameFrom(fileName: String): (Long, String) = {
    val parsed = fileName.split(" ", 2)
    val (id, name) = (parsed(0).toLong, parsed(1))
    (id, name)
  }
}
