package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto

object YamlFileMapper {
  
  /** Extracts meta from folder.
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
  
  /** Extracts meta from folder.
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
  
  def readWorkflowFile(path: Path): WorkflowFileFields = {
    val categoryYamlContent = readFileContent(path)
    val mapper: ObjectMapper = initMapper
    mapper.readValue(categoryYamlContent, classOf[WorkflowFileFields])
  }

  def readCategoryFile(path: Path): CategoryFileFields = {
    val categoryYamlContent = readFileContent(path)
    val mapper: ObjectMapper = initMapper
    mapper.readValue(categoryYamlContent, classOf[CategoryFileFields])
  }
  
  private def readFileContent(path: Path): String = {
    val source = scala.io.Source.fromFile(path.toFile)
    val fileContent = try source.mkString
    finally source.close()
    fileContent
  }
  
  private def fileNamesToMapWithFileContent(path: Path, list: Option[Iterable[String]]): Option[Map[String, String]] = {
    list.map { listNames =>
      listNames.map { fileName =>
        (fileName, readFileContent(path.resolve(fileName)))
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