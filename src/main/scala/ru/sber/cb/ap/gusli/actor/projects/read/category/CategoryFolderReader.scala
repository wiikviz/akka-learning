package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path
import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.WorkflowCreator.ReadSqlFile
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFilePathWorker

object CategoryFolderReader {
  def apply(meta: CategoryFolderReaderMeta): Props = Props(new CategoryFolderReader(meta))
  
  case class ReadCategoryFolder()
  
}

class CategoryFolderReader(meta: CategoryFolderReaderMeta) extends BaseActor {
  val filterFiles = scala.collection.mutable.ArrayBuffer("init.sql")
  
  override def receive: Receive = {
    case ReadCategoryFolder() =>
      addFilesFromYamlToFilter()
      val files = YamlFilePathWorker.getAllValidCategoryChilds(this.meta.path, filterFiles)
      files.foreach{p =>
        doIfWfFile(p)
        doIfWfFolder(p)
        doIfCategoryFolder(p)
      }
  }
  
  private def addFilesFromYamlToFilter(): Boolean = {true}
  private def doIfWfFile(path: Path): Unit = {
    if(path.toFile.isFile && path.getFileName.toString.endsWith(".sql")) {
      val wfCreator = createWorkflowCreator
      wfCreator ! ReadSqlFile()
    }
  }
  private def doIfWfFolder(path: Path) = {}
  private def doIfCategoryFolder(path: Path) = {}
  private def createWorkflowCreator = {
    context.actorOf(WorkflowCreator(WorkflowCreatorMetaDefault(this.meta.path, this.meta.category)))
  }
}

trait CategoryFolderReaderMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryFolderReaderMetaDefault(path: Path, category: ActorRef) extends CategoryFolderReaderMeta