package ru.sber.cb.ap.gusli.actor.projects

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature
import com.fasterxml.jackson.module.scala.DefaultScalaModule

package object yamlfiles {
  def initMapper: ObjectMapper =  new ObjectMapper(new YAMLFactory()
    .enable(Feature.MINIMIZE_QUOTES)
    .disable(Feature.WRITE_DOC_START_MARKER))
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
      .registerModule(DefaultScalaModule)
  
  def writeMetaToFile[T](path: Path, value: T): Unit = value match {
    case v => writeFieldsToFile(path, v)
  }
  
  def writeFieldsToFile[T](path: Path, fields: T): Unit = {
    val mapper = initMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
    val f = new File(path.toString)
    mapper.writeValue(f, fields)
  }
  
  def writeTextFileToDirectory(fileName: String, fileContent: String, dir: Path): Path =
    Files.write(dir.resolve(fileName), fileContent.getBytes(StandardCharsets.UTF_8))
}
