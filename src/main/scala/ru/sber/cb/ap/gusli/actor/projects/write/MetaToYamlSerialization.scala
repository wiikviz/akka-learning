package ru.sber.cb.ap.gusli.actor.projects.write

import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, EntityMeta}

object MetaToYamlSerialization {
  //  trait CategoryMeta {
  //    def name: String
  //    def sqlMap: Map[String, String]
  //    def init: Map[String, String]
  //    def user: Option[String]
  //    def queue: Option[String]
  //    def grenkiVersion: Option[String]
  //    def params: Map[String, String]
  //    def stats: Set[Long]
  //    def entities: Set[Long]
  //  }


//  case class WorkflowDto(name:     String,                                название файла или папки

//                         grenkiVersion: Option[String] = None,
//                         queue:    Option[String] = None,
//                         user:     Option[String] = None,

//                         sql:      Map[String, String],                   .sql    файлы вокруг
//                         sqlMap:   Map[String, String] = Map.empty,       .config файлы вокруг
//                         init:     Map[String, String] = Map.empty,       .hql    файлы вокруг  (с большой вероятностью .sql)

//                         params:   Map[String, String] = Map.empty,
//                         stats:    Set[Long] = Set.empty,
//                         entities: Set[Long] = Set.empty)

//  meta.yaml
//  ---
//  grenki: 0.2
//  queue: root.platform
//  user: pupkin

//  init:
//    - -init.hql
//    - init2.hql
//  map:
//    - map.config
//    - map2.config
//  sql:
//    - rb-vek5555sel.sql
//    - rb-car433ds.sql

//  param:
//    - p1:
//    - p2: I'm String
//  stats:
//    - 2
//    - 11
//  entities:
//    - 123
//    - -124

//  case class CategoryMetaDefault(name: String,
//                                 sqlMap: Map[String, String] = Map.empty,
//                                 init: Map[String, String] = Map.empty,
//                                 user: Option[String] = None,
//                                 queue: Option[String] = None,
//                                 grenkiVersion: Option[String] = None,
//                                 params: Map[String, String] = Map.empty,
//                                 stats: Set[Long] = Set.empty,
//                                 entities: Set[Long] = Set.empty
//                                )




  def getFilesContentFromCategory(metaDefault: CategoryMetaDefault, parentMetaDefault: CategoryMetaDefault): Map[String,String] = {




    Map.empty[String,String]

  }

  def convertWorkflowMetaToYAMLFileContent(meta: WorkflowDto, categoryMeta: CategoryMeta): String = {


    "WorkflowMeta  YAMLfile  Content"
  }


  //trait EntityMeta {
  //  def id: Long          в названии файла(или папки)
  //  def name: String      в названии файла(или папки)
  //  def path: String
  //  def parentId: Option[Long]  получаем из родительской папки
  //  def storage = "HDFS"        всегда "HDFS"
  //}
  def convertEntityMetaToYAMLFileContent(meta: EntityMeta): String = {
    s"""---
       |path: "${meta.path}"""".stripMargin
  }

  private def getChangeBeetweenTwoMaps(parent:Map[String,String], child:Map[String,String]):(Map[String,String],Set[String]) = {
    val parentMap = collection.mutable.Map.empty ++ parent
    val resultMap = collection.mutable.Map.empty[String,String]
    for( pair @  (fileName, fileContent) <- child ) {
      if(!parentMap.contains(fileName) || parentMap(fileName) != fileContent){
        resultMap += pair
      }
      parentMap -= fileName
    }
    (resultMap.toMap, parentMap.keys.toSet)
  }


  def main(args: Array[String]): Unit = {
    val first = Map("1"->"a", "2"->"br", "3"->"c", "4"->"d", "5"->"e")
    val second = Map("1"->"a", "2"->"b", "3"->"c", "6"->"f", "7"->"g")

    val result = getChangeBeetweenTwoMaps(first,second)
    println(result)
    assert(result._1.keys.toSet == Set("2","6","7"))
    assert(result._2 == Set("4","5"))
  }





}
