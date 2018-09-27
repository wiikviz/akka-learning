package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.CategoryCreator.{CategoryRead, ReadFolder}
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.{CategoryFolderRead, ReadCategoryFolder}
import ru.sber.cb.ap.gusli.actor.projects.read.category.create._
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorByFolder.{ReadWorkflowFolder, WorkflowFolderRead}
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorBySql.{ReadSqlFile, WorkflowFileRead}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFilePathWorker

object CategoryFolderReader {
  def apply(meta: CategoryFolderReaderMeta): Props = Props(new CategoryFolderReader(meta))
  
  case class ReadCategoryFolder(replyTo: Option[ActorRef] = None) extends Request
  
  case class CategoryFolderRead(replyTo: Option[ActorRef] = None) extends Response
}

class CategoryFolderReader(meta: CategoryFolderReaderMeta) extends BaseActor {
  val filterFiles: scala.collection.mutable.ArrayBuffer[String] = scala.collection.mutable.ArrayBuffer[String]("init.sql")
  private var childrenCount = 0
  private var answeredChildrenCount = 0
  

  override def receive: Receive = {
    case ReadCategoryFolder(replyTo) => this.meta.category ! GetCategoryMeta()
    
    case CategoryMetaResponse(meta) =>
      addFilesFromMetaToFilter(meta)
      val files = YamlFilePathWorker.getAllValidCategoryChilds(this.meta.path, filterFiles)
      childrenCount = files.size
      files.foreach { p =>
        doIfWfFile(p)
        doIfWfFolder(p)
        doIfCategoryFolder(p)
      }
      checkFinish()
    case CategoryRead(c) =>
      answeredChildrenCount += 1
      checkFinish()
    case WorkflowFileRead() | WorkflowFolderRead()=>
      answeredChildrenCount += 1
      checkFinish()
  }
  
  //TODO: boolean
  /**
    * Функция смотрит файлы в yaml файле и ищет их в директории. Если находит, то добавляет в поле-фильтр.
    *
    * @return true - if all files exists
    */
  private def addFilesFromMetaToFilter(meta: CategoryMeta) = filterFiles.++=(meta.init.keys).++=(meta.sqlMap.keys)
  
  private def doIfWfFile(path: Path): Unit =
    if (path.toFile.isFile && path.getFileName.toString.toLowerCase.endsWith(".sql"))
      createWorkflowCreatorBySql(path) ! ReadSqlFile()
  
  private def doIfWfFolder(path: Path): Unit =
    if (path.toFile.isDirectory && isWfFolder(path))
      createWorkflowCreatorByFolder(path) ! ReadWorkflowFolder()
  
  private def doIfCategoryFolder(path: Path): Unit =
    if (path.toFile.isDirectory && !isWfFolder(path))
      createCategoryCreator(path) ! ReadFolder()
  
  private def createWorkflowCreatorBySql(path: Path) =
    context.actorOf(WorkflowCreatorBySql(WorkflowCreatorBySqlBeSqlMetaDefault(path, this.meta.category)))
  
  private def createWorkflowCreatorByFolder(path: Path) =
    context.actorOf(WorkflowCreatorByFolder(WorkflowCreatorByFolderMetaDefault(path, this.meta.category)))
  
  private def createCategoryCreator(path: Path) =
    context.actorOf(CategoryCreator(CategoryCreatorMetaDefault(path, this.meta.category)))
  
  private def checkFinish(): Unit = if (answeredChildrenCount == childrenCount) {
    context.parent ! CategoryFolderRead()
    context.stop(self)
  }
  
  private def isWfFolder(path: Path) = path.getFileName.toString.toLowerCase.startsWith("wf-")
}

trait CategoryFolderReaderMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryFolderReaderMetaDefault(path: Path, category: ActorRef) extends CategoryFolderReaderMeta