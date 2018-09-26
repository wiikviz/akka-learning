package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.CategoryCreator.ReadFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.create._
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorByFolder.ReadWorkflowFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorBySql.ReadSqlFile
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFilePathWorker

object CategoryFolderReader {
  def apply(meta: CategoryFolderReaderMeta): Props = Props(new CategoryFolderReader(meta))
  
  case class ReadCategoryFolder(replyTo: Option[ActorRef] = None) extends Request
  
}

class CategoryFolderReader(meta: CategoryFolderReaderMeta) extends BaseActor {
  val filterFiles: scala.collection.mutable.ArrayBuffer[String] = scala.collection.mutable.ArrayBuffer[String]("init.sql")
  
  override def preStart(): Unit = {
    super.preStart()
//    filterFiles ++= scala.collection.mutable.ArrayBuffer[String]("init.sql")
  }
  
  override def receive: Receive = {
    case ReadCategoryFolder(replyTo) => this.meta.category ! GetCategoryMeta()
    
    case CategoryMetaResponse(meta) =>
      addFilesFromMetaToFilter(meta)
      val files = YamlFilePathWorker.getAllValidCategoryChilds(this.meta.path, filterFiles)
      files.foreach { p =>
        doIfWfFile(p)
        doIfWfFolder(p)
        doIfCategoryFolder(p)
      }
  }
  
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
  
  private def isWfFolder(path: Path) = path.getFileName.toString.toLowerCase.startsWith("wf-")
}

trait CategoryFolderReaderMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryFolderReaderMetaDefault(path: Path, category: ActorRef) extends CategoryFolderReaderMeta