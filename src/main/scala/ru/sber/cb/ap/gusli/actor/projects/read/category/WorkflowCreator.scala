package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.projects.read.category.WorkflowCreator.{FileRead, FolderRead, ReadFolder, ReadSqlFile}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowCreator {
  def apply(meta: WorkflowCreatorMeta): Props = Props(new WorkflowCreator(meta))
  
  case class ReadSqlFile(replyTo: Option[ActorRef] = None) extends Request
  case class ReadFolder(replyTo: Option[ActorRef] = None) extends Response
  
  case class FileRead(replyTo: Option[ActorRef] = None) extends Response
  case class FolderRead(replyTo: Option[ActorRef] = None) extends Response
}

class WorkflowCreator(meta: WorkflowCreatorMeta) extends BaseActor {
  override def receive: Receive = {
    case ReadSqlFile(replyTo) => replyTo.getOrElse(sender) ! FileRead()
    case ReadFolder(replyTo) => replyTo.getOrElse(sender) ! FolderRead()
  }
}

trait WorkflowCreatorMeta {
  val path: Path
  val category: ActorRef
}

case class WorkflowCreatorMetaDefault(path: Path, category: ActorRef) extends WorkflowCreatorMeta