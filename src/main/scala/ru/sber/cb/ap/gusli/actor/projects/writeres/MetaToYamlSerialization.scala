package ru.sber.cb.ap.gusli.actor.projects.writeres

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, WorkflowMeta}

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


  //  trait WorkflowMeta {
  //    def grenkiVersion: Option[String]
  //    def queue: Option[String]
  //    def user: Option[String]
  //    def init: Map[String, String]
  //    def sql: Map[String, String]
  //    def sqlMap: Map[String, String]

  //    def name: String  // название файла или папки





  //    def params: Map[String, String]
  //    def stats: Set[Long]
  //  }

//  meta.yaml
//  ---
//  grenki: 0.2
//  queue: root.platform
//  user: pupkin
//  init:
//    - init.hql
//    - init2.hql
//  map:
//    - map.config
//    - map2.config
//
//  sql:
//    - rb-vek5555sel.sql
//    - rb-car433ds.sql
//  param:
//    - p1: 1
//    - p2: I'm String
//  stats:
//    - 2
//    - 11
//  entities:
//    - 123
//    - 124





  //trait EntityMeta {
  //  def id: Long
  //  def name: String
  //  def path: String
  //  def parentId: Option[Long]
  //  def storage = "HDFS"
  //}
  def convertCategoryMetaToYAMLFileContent(meta: CategoryMeta, parentMeta: CategoryMeta): String = {


    "CategoryMeta  YAMLfile  Content"
  }

  def convertWorkflowMetaToYAMLFileContent(meta: WorkflowMeta, categoryMeta: CategoryMeta): String = {


    "WorkflowMeta  YAMLfile  Content"
  }

  def convertEntityMetaToYAMLFileContent(meta: EntityMeta, parentMeta: EntityMeta): String = {
    s"""---
       |name: "${meta.name}"
       |path: "${meta.path}"
       |id: ${meta.id}
       |storage: "${meta.storage}"
       |parentId: ${meta.parentId}""".stripMargin
  }
}
