package ru.sber.cb.ap.gusli.actor.projects

import java.io.File

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import java.nio.file.{Files, Paths}

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

object SerializationFromYamlWithMergeCaseClasses extends App {
  /* TODO: Не сериализуются параметры*/
  case class Heh(name: String,
    sql: List[String],
    sqlMap: List[String] = List("ChangeMe"),
    init: List[String] = Nil,
    user: Option[String] = Some("ChangeMe"),
    queue: Option[String] = None,
    grenkiVersion: Option[String] = None,
    params: Map[String, String] = Map.empty) {
    override def toString() = s"\nname: " + name +
      s"\nsql: " + sql +
      s"\nsqlMap: " + sqlMap +
      s"\ninit: " + init +
      s"\nuser: " + user +
      s"\nqueue: " + queue +
      s"\ngrenkiVersion: " + grenkiVersion +
      s"\nparams: " + params
  }
  
  val source = scala.io.Source.fromFile("C:\\Projects\\Scala\\gucli\\src\\test\\resources\\project_test-2\\category\\meta.yaml")
  val lines = try source.mkString finally source.close()
  
  val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
  
  mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  mapper.registerModule(DefaultScalaModule)
  
  val hehPar: Heh = Heh("a", List("a"), List("a"), List("a"), Some("a"), Some("a"), Some("a"), Map("a" -> "a"))
  val hehChild: Heh = Heh(null, List("b"), List("b"), Nil)
  val hehFile: Heh = mapper.readValue(lines, classOf[Heh])
  
  val Heh(a,b,c,d,e,f,g,h) = hehPar
  val Heh(i,j,c2,d2,e2,f2,g2,h2) = hehPar
  
  val tik = mergeCaseClasses(hehFile, hehPar)
  
  private def mergeCaseClasses(classChild: Heh, classParent: Heh) = {
    classChild.copy(
      name = if (classChild.name != null) classChild.name else classParent.name,
      sql = if (classChild.sql != null) classChild.sql else classParent.sql,
      sqlMap = if (classChild.sqlMap != null) classChild.sqlMap else classParent.sqlMap,
      init = if (classChild.init != null) classChild.init else classParent.init,
      user = if (classChild.user != None) classChild.user else classParent.user,
      queue = if (classChild.queue != None) classChild.queue else classParent.queue,
      grenkiVersion = if (classChild.grenkiVersion != None) classChild.grenkiVersion else classParent.grenkiVersion,
      params = if (classChild.params != null) classChild.params else classParent.params
    )
  }
  
  println(hehFile)
  println(hehPar)
  println(tik)
}
