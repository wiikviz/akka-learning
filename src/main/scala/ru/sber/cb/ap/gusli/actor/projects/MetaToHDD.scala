package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, WorkflowMeta}

object MetaToHDD {
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
  def writeCategoryMetaToPath(meta:CategoryMeta, dir:Path, parentMeta:CategoryMeta): Path ={
    val categoryFolder =  Files createDirectories dir resolve meta.name.replace("-", "-")




    categoryFolder
  }




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
  def writeWorkflowMetaToPath(meta:WorkflowMeta, dir:Path, categoryMeta: CategoryMeta): Path ={
    val workflowFolder = Files createDirectories dir resolve meta.name.replace("-","-")





    workflowFolder
  }





  //trait EntityMeta {
  //  def id: Long
  //  def name: String
  //  def path: String
  //  def parentId: Option[Long]
  //  def storage = "HDFS"
  //}
  def writeEntityMetaToPath(meta: EntityMeta, dir:Path, parentMeta:EntityMeta, hasChildren:Boolean): Path ={
    val entityFolder = Files createDirectories dir resolve meta.name.replace("-","-")





    entityFolder
  }
//  def



}
