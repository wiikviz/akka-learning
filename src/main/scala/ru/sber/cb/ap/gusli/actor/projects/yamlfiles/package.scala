package ru.sber.cb.ap.gusli.actor.projects

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature
import com.fasterxml.jackson.module.scala.DefaultScalaModule

package object yamlfiles {
  def initMapper: ObjectMapper = {
    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory().enable(Feature.MINIMIZE_QUOTES))
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
      .registerModule(DefaultScalaModule)
    mapper
  }
}
