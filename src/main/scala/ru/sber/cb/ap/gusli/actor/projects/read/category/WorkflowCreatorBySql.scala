package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddWorkflow, CategoryMetaResponse, GetCategoryMeta, WorkflowCreated}
import ru.sber.cb.ap.gusli.actor.core.Workflow.BindEntity
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, WorkflowMeta, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.category.WorkflowCreatorBySql._
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowCreatorBySql {
  def apply(meta: WorkflowCreatorBeSqlMeta): Props = Props(new WorkflowCreatorBySql(meta))
  
  case class ReadSqlFile(replyTo: Option[ActorRef] = None) extends Request
  
  case class FileRead(sendTo: Option[ActorRef] = None) extends Response
  
}

class WorkflowCreatorBySql(meta: WorkflowCreatorBeSqlMeta) extends BaseActor {
  val entities = scala.collection.mutable.ArrayBuffer[Long]()
  
  override def receive: Receive = {
    case ReadSqlFile(replyTo) =>
      this.meta.category ! GetCategoryMeta()
    
    case CategoryMetaResponse(meta) => {
      //readFile
      //fillEntities
      this.meta.category ! AddWorkflow(createWfMetaFromWfFolder())
    }

    case WorkflowCreated(wf) =>
      entities.foreach(wf ! BindEntity(_))
      
  }
  
  private def createWfMetaFromParentCategory(): WorkflowMeta = WorkflowMetaDefault("test", Map.empty)
  
  private def createWfMetaFromWfFolder(): WorkflowMeta = WorkflowMetaDefault("test", Map.empty)
  
}

trait WorkflowCreatorBeSqlMeta {
  val path: Path
  val category: ActorRef
}

case class WorkflowCreatorBySqlBeSqlMetaDefault(path: Path, category: ActorRef) extends WorkflowCreatorBeSqlMeta