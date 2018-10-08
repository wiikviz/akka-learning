package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.io.{File, FilenameFilter}
import java.nio.file.{Path, Paths}

import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig

object YamlFilePathWorker {
  
  def extractParentIdFromPath(path : Path): Option[Long] = {
    val fileName = path.getFileName.toString.toLowerCase
    val parentName = path.getParent.getFileName.toString.toLowerCase
    
    if (fileName == DirectoryReadWriteConfig.entityMetaFileName)
      extractParentIdFromPath(path.getParent)
    else if (fileName == "entity")
      None
    else if (parentName == "entity")
      Some(0L)
    else {
      val (id, name) = this.parseIdAndNameFrom(parentName)
      Some(id)
    }
  }
  
  def isYaml(path: Path): Boolean = {
    path.toFile.isFile && path.getFileName.toString.toLowerCase.endsWith(".yaml")
  }
  
  def parseIdAndNameFromYaml(path: Path): (Long, String) =
    parseIdAndNameFromYaml(path.getFileName.toString)
  
  
  def parseIdAndNameFromYaml(fileName: String): (Long, String) = {
    //Use dropRight(5) for cut ".yaml" in the end and don't touch in the middle if exists
    parseIdAndNameFrom(fileName.dropRight(5))
  }
  
  def parseIdAndNameFrom(fileName: String): (Long, String) = {
    val parsed = fileName.split(" ", 2)
    val (id, name) = (parsed(0).toLong, parsed(1))
    (id, name)
  }
  
  def parseIdAndNameFrom(path: Path): (Long, String) = {
    parseIdAndNameFrom(path.getFileName.toString)
  }
  
  def getAllValidEntityChilds(path: Path): List[Path] = {
    val files = path.toFile.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = name.matches("[0-9]+\\s.*")
    })
    files.map(_.toPath).toList
  }
  
  def getAllValidCategoryChilds(path: Path, filterNames: scala.collection.mutable.ArrayBuffer[String]): List[Path] = {
    val files = path.toFile.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean =
        (path.resolve(name).toFile.isDirectory || name.toLowerCase.endsWith(".sql")) && !filterNames.map(_.toLowerCase).contains(name.toLowerCase)
    })
    files.map(_.toPath).toList
  }
  
  def isEntityYaml(path: Path): Boolean = path.getFileName.toString.toLowerCase == DirectoryReadWriteConfig.entityMetaFileName
}