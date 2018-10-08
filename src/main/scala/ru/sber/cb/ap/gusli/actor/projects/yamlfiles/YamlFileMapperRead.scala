package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.ObjectMapper
import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.read.util.FileContentReader

object YamlFileMapperRead {
  
  /** Extracts completed meta from folder.
    *
    * @param path path to folder
    * @return Meta from File if it exists, None otherwise
    */
  def readToCategoryMeta(path: Path): Option[CategoryMetaDefault] = {
    val catName = path.getFileName.toString
    val metaFilePath = path.resolve(DirectoryReadWriteConfig.categoryMetaFileName)
    
    if (!metaFilePath.toFile.exists())
      None
    else {
      val deserializedCat = readCategoryFile(metaFilePath)
  
      Some(CategoryMetaDefault(
        catName,
        fileNamesToMapWithFileContent(path, deserializedCat.map).getOrElse(Map.empty),
        fileNamesToMapWithFileContent(path, deserializedCat.init).getOrElse(Map.empty),
        deserializedCat.user,
        deserializedCat.queue,
        deserializedCat.grenki,
        deserializedCat.param.get,
        deserializedCat.stats.get.map(_.toLong),
        deserializedCat.entities.get.map(_.toLong)
      ))
    }
  }
  
  /** Extracts completed meta from folder.
    *
    * @param path path to folder
    * @return Meta from File if it exists, None otherwise
    */
  def readToWorkflowDtoMeta(path: Path): Option[WorkflowDto] = {
    val wfName = path.getFileName.toString.replaceFirst(DirectoryReadWriteConfig.workflowFolderPrefix, "")
    val metaFilePath = path.resolve(DirectoryReadWriteConfig.workflowMetaFileName)
  
    if (!metaFilePath.toFile.exists())
      None
    else {
      val fileFields = readWorkflowFile(metaFilePath)
      
      Some(WorkflowDto(
        wfName,
        fileNamesToMapWithFileContent(path, fileFields.sql).getOrElse(Map.empty),
        fileNamesToMapWithFileContent(path, fileFields.map).getOrElse(Map.empty),
        fileNamesToMapWithFileContent(path, fileFields.init).getOrElse(Map.empty),
        fileFields.user,
        fileFields.queue,
        fileFields.grenki,
        fileFields.param.get,
        fileFields.stats.get.map(_.toLong),
        fileFields.entities.get.map(_.toLong)
      ))
    }
  }
  
  /** Extracts fields to meta-like case class with optional fields from folder.
    *
    * @param path path to folder
    * @return Meta from File if it exists, None otherwise
    */
  def readToCategoryOptionalFields(path: Path, metaFileName: String = DirectoryReadWriteConfig.categoryMetaFileName): Option[CategoryOptionalFields] = {
    val catName = path.getFileName.toString
    val metaFilePath = path.resolve(metaFileName)
    
    if (!metaFilePath.toFile.exists)
      None
    else {
      val fileFields = readCategoryFile(metaFilePath)
      
      Some(CategoryOptionalFields(
        name = catName,
        grenkiVersion = fileFields.grenki,
        sqlMap = fileNamesToMapWithFileContent(path, fileFields.map),
        init = fileNamesToMapWithFileContent(path, fileFields.init),
        user = fileFields.user,
        queue = fileFields.queue,
        params = fileFields.param,
        stats = makeSetLongOrNone(fileFields.stats),
        entities = makeSetLongOrNone(fileFields.entities)
      ))
    }
  }
  
  /** Extracts fields to meta-like case class with optional fields from folder.
    *
    * @param path path to folder
    * @return Meta from File if it exists, None otherwise
    */
  def readToWorkflowOptionDto(path: Path, metaFileName: String = DirectoryReadWriteConfig.workflowMetaFileName): Option[WorkflowOptionDto] = {
    val wfName = path.getFileName.toString.replaceFirst(DirectoryReadWriteConfig.workflowFolderPrefix, "")
    val metaFilePath = path.resolve(metaFileName)
  
    if (!metaFilePath.toFile.exists())
      None
    else {
      val fileFields = readWorkflowFile(metaFilePath)
      
      Some(WorkflowOptionDto(
        name = Some(wfName),
        grenkiVersion = fileFields.grenki,
        sql = fileNamesToMapWithFileContent(path, fileFields.sql),
        sqlMap = fileNamesToMapWithFileContent(path, fileFields.map),
        init = fileNamesToMapWithFileContent(path, fileFields.init),
        user = fileFields.user,
        queue = fileFields.queue,
        params = fileFields.param,
        stats = makeSetLongOrNone(fileFields.stats),
        entities = makeSetLongOrNone(fileFields.entities)
      ))
    }
  }
  
  def readWorkflowFile(path: Path): WorkflowFileFields = {
    val categoryYamlContent = FileContentReader.readFileContent(path)
    val mapper: ObjectMapper = initMapper
    mapper.readValue(categoryYamlContent, classOf[WorkflowFileFields])
  }
  
  def readCategoryFile(path: Path): CategoryFileFields = {
    val categoryYamlContent = FileContentReader.readFileContent(path)
    val mapper: ObjectMapper = initMapper
    mapper.readValue(categoryYamlContent, classOf[CategoryFileFields])
  }
  
  private def fileNamesToMapWithFileContent(path: Path, list: Option[Iterable[String]]): Option[Map[String, String]] = {
    val deleteSymbol = DirectoryReadWriteConfig.deleteSymbol
    list.map { listNames =>
      listNames.map { fileName => if (fileName.startsWith(deleteSymbol))
        (fileName.substring(deleteSymbol.length), deleteSymbol)
        else
        (fileName, FileContentReader.readFileContent(path.resolve(fileName)))
      }.toMap[String, String]
    }
  }
  
  private def makeSetLongOrNone(s: Option[Set[Int]]): Option[Set[Long]] = {
    if (s.nonEmpty) Some(s.get.map(_.toLong))
    else None
  }
}
