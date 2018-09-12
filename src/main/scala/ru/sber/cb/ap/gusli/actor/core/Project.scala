package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object Project {
  def apply(meta: ProjectMeta): Props = Props(new Project(meta))

  case class GetMeta(replyTo: Option[ActorRef] = None) extends Request

  case class GetCategoryRoot(replyTo: Option[ActorRef] = None) extends Request

  case class GetEntityRoot(replyTo: Option[ActorRef] = None) extends Request

  case class ProjectMetaResponse(name: String) extends Response with ProjectMeta

  case class CategoryRoot(root: ActorRef) extends Response

  case class EntityRoot(root: ActorRef) extends Response

}

class Project(meta: ProjectMeta) extends BaseActor {
  import Project._
  override def receive: Receive = {
    case GetMeta(replyTo) => replyTo.getOrElse(sender()) ! ProjectMetaResponse(meta.name)
  }
}

trait ProjectMeta {
  def name: String
}

case class ProjectMetaDefault(name:String) extends ProjectMeta
