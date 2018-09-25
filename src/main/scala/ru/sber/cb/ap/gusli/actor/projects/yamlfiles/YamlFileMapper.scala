package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.projects.read.util.FileContentReader

object YamlFileMapper {
  
  /** Extracts completed meta from folder.
    *
    * @param path path to folder
    * @return Meta from File if it exists, None otherwise
    */
  def readToCategoryMeta(path: Path): Option[CategoryMetaDefault] = {
    val catName = path.getFileName.toString
    val metaFilePath = path.resolve("meta.yaml")
    
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
    val wfName = path.getFileName.toString.replaceFirst("wf-", "")
    val metaFilePath = path.resolve("meta.yaml")
  
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
  def readToCategoryOptionalFields(path: Path, metaFileName: String = "meta.yaml"): Option[CategoryOptionalFields] = {
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
  def readToWorkflowOptionDto(path: Path, metaFileName: String = "meta.yaml"): Option[WorkflowOptionDto] = {
    val wfName = path.getFileName.toString.replaceFirst("wf-", "")
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
  
  private def makeSetLongOrNone(intIterable: Option[Set[Int]]): Option[Set[Long]] = {
    if (intIterable.nonEmpty) Some(intIterable.get.map(_.toLong))
    else None
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
    list.map { listNames =>
      listNames.map { fileName =>
        (fileName, FileContentReader.readFileContent(path.resolve(fileName)))
      }.toMap[String, String]
    }
  }
  
  private def initMapper = {
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .registerModule(DefaultScalaModule)
    mapper
  }
}
//Single: "" -> Some(), empty -> None
//List: [] -> Some(Set()), empty -> None
//MapElem: "" -> "", empty-elem -> null
//Map: {} -> Some(Map()), empty -> None
case class CategoryFileFields(
grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[List[String]] = Some(List.empty),
  map: Option[List[String]] = Some(List.empty),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty)
) extends generalFileFields(grenki, queue, user, init, map, param, stats, entities)

case class WorkflowFileFields(
  grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[List[String]] = Some(List.empty),
  map: Option[List[String]] = Some(List.empty),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty),
  sql: Option[Set[String]]
) extends generalFileFields(grenki, queue, user, init, map, param, stats, entities) {
  
  override def toString(): String = super.toString() + "\nsql:" + sql
}

case class WorkflowOptionDto(
  name: Option[String] = None,
  grenkiVersion: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[Map[String, String]] = Some(Map.empty),
  sqlMap: Option[Map[String, String]] = Some(Map.empty),
  params: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Long]] = Some(Set.empty),
  entities: Option[Set[Long]] = Some(Set.empty),
  sql: Option[Map[String, String]]
)

case class CategoryOptionalFields(
  name: String,
  grenkiVersion: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[Map[String, String]] = Some(Map.empty),
  sqlMap: Option[Map[String, String]] = Some(Map.empty),
  params: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Long]] = Some(Set.empty),
  entities: Option[Set[Long]] = Some(Set.empty)
)

abstract class generalFileFields(grenki: Option[String],
  queue: Option[String],
  user: Option[String],
  init: Option[List[String]],
  map: Option[List[String]],
  param: Option[Map[String, String]],
  stats: Option[Set[Int]],
  entities: Option[Set[Int]]) {
  
  override def toString() = {
    s"\ngrenki: " + grenki +
      s"\nqueue: " + queue +
      s"\nuser: " + user +
      s"\ninit: " + init +
      s"\nmap: " + map +
      s"\nparam: " + param +
      s"\nstats " + stats +
      s"\nentities: " + entities
  }
}