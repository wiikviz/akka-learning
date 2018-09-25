package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.BaseActor
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
  
  case class ReadCategoryFolder()
  
}

class CategoryFolderReader(meta: CategoryFolderReaderMeta) extends BaseActor {
  private val filterFiles = scala.collection.mutable.ArrayBuffer[String]("init.sql")
  
  override def receive: Receive = {
    case ReadCategoryFolder() => this.meta.category ! GetCategoryMeta()
    
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
      createWorkflowCreatorBySql ! ReadSqlFile()
  
  private def doIfWfFolder(path: Path): Unit =
    if (path.toFile.isDirectory && isWfFolder(path))
      createWorkflowCreatorByFolder ! ReadWorkflowFolder()
  
  private def doIfCategoryFolder(path: Path): Unit =
    if (path.toFile.isDirectory && !isWfFolder(path))
      createCategoryCreator ! ReadFolder()
  
  private def createWorkflowCreatorBySql =
    context.actorOf(WorkflowCreatorBySql(WorkflowCreatorBySqlBeSqlMetaDefault(this.meta.path, this.meta.category)))
  
  private def createWorkflowCreatorByFolder =
    context.actorOf(WorkflowCreatorByFolder(WorkflowCreatorByFolderMetaDefault(this.meta.path, this.meta.category)))
  
  private def createCategoryCreator =
    context.actorOf(CategoryCreator(CategoryCreatorMetaDefault(this.meta.path, this.meta.category)))
  
  private def isWfFolder(path: Path) = path.getFileName.toString.toLowerCase.startsWith("wf-")
}

trait CategoryFolderReaderMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryFolderReaderMetaDefault(path: Path, category: ActorRef) extends CategoryFolderReaderMeta