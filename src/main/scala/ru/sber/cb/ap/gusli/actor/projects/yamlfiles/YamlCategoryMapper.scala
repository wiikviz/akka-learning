package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault

object YamlCategoryMapper {
  
  def readToCategoryMeta(path: Path) = {
    val catName = path.getFileName.toString
    val deserializedCat = read(path.resolve("category.yaml"))
  
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
  
  private def fileNamesToMapWithFileContent(path: Path, list: Option[Iterable[String]]): Option[Map[String, String]] = {
    list.map { listNames =>
      listNames.map { fileName =>
        (fileName, readFileContent(path.resolve(fileName)))
      }.toMap[String, String]
    }
  }
  
  def read(path: Path): CategoryFile = {
    val source = scala.io.Source.fromFile(path.toFile)
    val categoryYamlContent = try source.mkString
    finally source.close()
    this.read(categoryYamlContent)
  }
  
  def read(categoryYamlContent: String): CategoryFile = {
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
    
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    mapper.registerModule(DefaultScalaModule)
    
    mapper.readValue(categoryYamlContent, classOf[CategoryFile])
  }
  private def readFileContent(path: Path): String = {
    val source = scala.io.Source.fromFile(path.toFile)
    val fileContent = try source.mkString
    finally source.close()
    fileContent
  }
}

case class CategoryFile(
  grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = Some("ChangeMe"),
  init: Option[List[String]] = Some(Nil),
  map: Option[List[String]] = Some(List("ChangeMe")),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty)) {
  
  override def toString() =
    s"\ngrenki: " + grenki +
      s"\nqueue: " + queue +
      s"\nuser: " + user +
      s"\ninit: " + init +
      s"\nmap: " + map +
      s"\nparam: " + param +
      s"\nstats " + stats +
      s"\nentities: " + entities
}