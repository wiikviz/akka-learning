package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object DirectoryProjectReader {
  def apply(): Props = Props(new DirectoryProjectReader())
  
  case class ReadProject(path: Path, replyTo: Option[ActorRef] = None) extends Request
  case class ProjectReaded(actorRef: ActorRef) extends Response
  case class ProjectNotReaded(path: Path) extends Response
}

class DirectoryProjectReader() extends BaseActor {
  
  import DirectoryProjectReader._
  
  override def receive: Receive = {
    case ReadProject(path: Path, sendTo: Option[ActorRef]) =>
      // sendTo getOrElse sender ! ProjectReaded(created-project)
      // val entitiesPath = path.resolve("entities")
      // val workflowsPath = path.resolve("workflows")
  }
}