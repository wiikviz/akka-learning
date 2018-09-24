package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object YamlCategoryMapper {
  
  def read(path: Path): CategoryFile = {
    //"./src/test/resources/project_test-2/category/category.yaml"
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
}

case class CategoryFile(
  grenki: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = Some("ChangeMe"),
  init: List[String] = Nil,
  map: List[String] = List("ChangeMe"),
  param: Map[String, Any] = Map.empty,
  stats: List[Int],
  entities: List[Int]) {
  
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