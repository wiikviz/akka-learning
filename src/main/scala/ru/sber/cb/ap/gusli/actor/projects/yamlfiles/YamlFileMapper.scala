package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault

object YamlFileMapper {
  
  def readToCategoryMeta(path: Path) = {
    val catName = path.getFileName.toString
    val deserializedCat = readCategoryFile(path.resolve("meta.yaml"))
  
    CategoryMetaDefault(
      catName,
      fileNamesToMapWithFileContent(path, deserializedCat.map).getOrElse(Map.empty),
      fileNamesToMapWithFileContent(path, deserializedCat.init).getOrElse(Map.empty),
      deserializedCat.user,
      deserializedCat.queue,
      deserializedCat.grenki,
      deserializedCat.param.get,
      deserializedCat.stats.get.map(_.toLong),
      deserializedCat.entities.get.map(_.toLong)
    )
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
    
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    mapper.registerModule(DefaultScalaModule)
    mapper
  }
}

case class CategoryFileFields(
grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = Some("ChangeMe"),
  init: Option[List[String]] = Some(Nil),
  map: Option[List[String]] = Some(List("ChangeMe")),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty)) extends generalFileFields

case class WorkflowFileFields(
  grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = Some("ChangeMe"),
  init: Option[List[String]] = Some(Nil),
  map: Option[List[String]] = Some(List("ChangeMe")),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty),
  sql: Option[Set[String]]
) extends generalFileFields {
  
  override def toString(): String = super.toString() + "\nsql:" + sql
}

abstract class generalFileFields(grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = Some("ChangeMe"),
  init: Option[List[String]] = Some(Nil),
  map: Option[List[String]] = Some(List("ChangeMe")),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty)) {
  
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