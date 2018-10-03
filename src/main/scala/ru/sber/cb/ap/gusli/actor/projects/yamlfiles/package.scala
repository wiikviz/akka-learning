package ru.sber.cb.ap.gusli.actor.projects

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

package object yamlfiles {
  def initMapper: ObjectMapper = {
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .registerModule(DefaultScalaModule)
    mapper
  }
}
