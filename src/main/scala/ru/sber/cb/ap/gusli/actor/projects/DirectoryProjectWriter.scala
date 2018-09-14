package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object DirectoryProjectWriter {
  def apply(): Props = Props(new DirectoryProjectWriter())
  
  case class WriteProject(path: Path, replyTo: Option[ActorRef] = None) extends Request
  
  case class ProjectWrited() extends Response
  
  case class ProjectNotWrited() extends Response
}

class DirectoryProjectWriter() extends BaseActor {
  
  import DirectoryProjectWriter._
  
  override def receive: Receive = {
    case WriteProject(path: Path, sendTo: Option[ActorRef]) => // sendTo getOrElse sender ! ProjectReaded(created-project)
  }
}