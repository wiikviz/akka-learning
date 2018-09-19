package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.Path

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import ru.sber.cb.ap.gusli.actor.core.EntityMetaDefault

import scala.beans.BeanProperty
import scala.io.Source


object YamlEntityMapper extends App {
  def read(path: Path): EntityMetaDefault = {
    val source = Source fromFile path.toFile
    val lines = try source.mkString finally source.close()
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    mapper.registerModule(DefaultScalaModule)
    
    val entityMetaFromYaml = mapper.readValue(lines, classOf[EntityFromYaml])
    
    EntityMetaDefault(0, null, entityMetaFromYaml.path.getOrElse(""), YamlFilePathWorker.extractParentIdFromPath(path))
  }
  
  def read(path: Path, id: Long, name: String): EntityMetaDefault = {
    this.read(path).copy(id = id, name = name)
  }
}

class EntityFromYaml {
  @BeanProperty var path: Option[String] = None
}
