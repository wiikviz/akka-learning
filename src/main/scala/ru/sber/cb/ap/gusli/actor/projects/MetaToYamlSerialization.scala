package ru.sber.cb.ap.gusli.actor.projects

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
  //    def name: String
  //    def sql: Map[String, String]
  //    def sqlMap: Map[String, String]
  //    def init: Map[String, String]
  //    def user: Option[String]
  //    def queue: Option[String]
  //    def grenkiVersion: Option[String]
  //    def params: Map[String, String]
  //    def stats: Set[Long]
  //    def entities: Set[Long]
  //  }

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
