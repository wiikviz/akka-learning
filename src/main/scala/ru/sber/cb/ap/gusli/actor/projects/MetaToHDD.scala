package ru.sber.cb.ap.gusli.actor.projects

import java.net.URLEncoder
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, ProjectMeta, WorkflowMeta}

object MetaToHDD {

  def writeProjectMetaToPath(meta: ProjectMeta, path: Path):Path =
    createNewFolder(meta.name, path)

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
    val categoryFolder =  createNewFolder(meta.name, dir)




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
    val workflowFolder = createNewFolder(meta.name, dir)






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
    val entityFolder = createNewFolder(meta.name, dir)






    entityFolder
  }

  private def createNewFolder(newFolderName:String, dirWereWillBeCreatedNewFilder:Path): Path = {
    Files createDirectories dirWereWillBeCreatedNewFilder resolve normalizeName(newFolderName)
  }

  private def normalizeName(folderName: String):String = {
    val tt = folderName.replace("/","~").replace(":","%3A").trim
    URLEncoder.encode(folderName, "UTF-8")
  }
}
