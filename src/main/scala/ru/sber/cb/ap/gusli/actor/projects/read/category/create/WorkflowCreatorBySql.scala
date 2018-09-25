package ru.sber.cb.ap.gusli.actor.projects.read.category.create

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddWorkflow, CategoryMetaResponse, GetCategoryMeta, WorkflowCreated}
import ru.sber.cb.ap.gusli.actor.core.Workflow.BindEntity
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, WorkflowMeta, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.category.ParentCategoryMetaComparator
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorBySql._
import ru.sber.cb.ap.gusli.actor.projects.read.util.FileContentReader
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowCreatorBySql {
  def apply(meta: WorkflowCreatorBeSqlMeta): Props = Props(new WorkflowCreatorBySql(meta))
  
  case class ReadSqlFile(replyTo: Option[ActorRef] = None) extends Request
  
  case class FileRead(sendTo: Option[ActorRef] = None) extends Response
  
}

class WorkflowCreatorBySql(meta: WorkflowCreatorBeSqlMeta) extends BaseActor {
  private val entities = scala.collection.mutable.ArrayBuffer[Long]()
  
  override def receive: Receive = {
    case ReadSqlFile(replyTo) => this.meta.category ! GetCategoryMeta()
    
    case CategoryMetaResponse(meta) =>
      entities ++= meta.entities
      this.meta.category ! AddWorkflow(createWfMeta(meta))

    case WorkflowCreated(wf) => entities.foreach(wf ! BindEntity(_))
  }
  
  private def createWfMetaFromParentCategory(): WorkflowMeta = WorkflowMetaDefault("test", Map.empty)
  
  private def createWfMeta(meta: CategoryMeta): WorkflowMeta = {
    val wfName = this.meta.path.getFileName.toString
    val wfSql = FileContentReader.readFileContent(this.meta.path)
    
    val wfMetaTemp = WorkflowMetaDefault(
      name = wfName,
      sql = Map(wfName -> wfSql)
    )
    
    ParentCategoryMetaComparator.workflowEmptyMeta(meta, wfMetaTemp)
  }
}

trait WorkflowCreatorBeSqlMeta {
  val path: Path
  val category: ActorRef
}

case class WorkflowCreatorBySqlBeSqlMetaDefault(path: Path, category: ActorRef) extends WorkflowCreatorBeSqlMeta