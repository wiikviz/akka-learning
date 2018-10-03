package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.io.File
import java.nio.file.{Path, Paths}

import com.fasterxml.jackson.annotation.JsonInclude

object YamlFileMapperWrite {

  
  
  private def writeFieldsToFile[T](path: Path, fields: T): Unit = {
    val mapper = initMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
    val f = new File(path.toString)
    mapper.writeValue(f, fields)
  }
}
