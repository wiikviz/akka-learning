package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, WorkflowCreated}
import ru.sber.cb.ap.gusli.actor.projects.read.category.WorkflowCreatorByFolder.{ReadWorkflowFolder, WorkflowRead}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowCreatorByFolder {
  def apply(meta: WorkflowCreatorByFolderMeta): Props = Props(new WorkflowCreatorByFolder(meta))
  
  case class ReadWorkflowFolder(replyTo: Option[ActorRef] = None) extends Request
  
  case class WorkflowRead(replyTo: Option[ActorRef] = None) extends Response
  
}

class WorkflowCreatorByFolder(meta: WorkflowCreatorByFolderMeta) extends BaseActor {
  override def receive: Receive = {
    case ReadWorkflowFolder(replyTo) =>
    case CategoryMetaResponse(replyTo) =>
    case WorkflowCreated(replyTo) =>
  }
}

trait WorkflowCreatorByFolderMeta {
  val path: Path
  val category: ActorRef
}

case class WorkflowCreatorByFolderMetaDefault(path: Path, category: ActorRef) extends WorkflowCreatorByFolderMeta
    