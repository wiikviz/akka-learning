package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.search.EntitySearcher
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object Project {
  def apply(meta: ProjectMeta): Props = {
    Props(new Project(meta))
  }

  case class GetProjectMeta(replyTo: Option[ActorRef] = None) extends Request

  case class GetCategoryRoot(replyTo: Option[ActorRef] = None) extends Request

  //entity requests
  case class GetEntityRoot(replyTo: Option[ActorRef] = None) extends Request

  case class FindEntity(entityId: Long, replyTo: Option[ActorRef] = None) extends Request

  //entity responses
  case class EntityFound(meta: EntityMeta, entityRef: ActorRef) extends Response

  case class EntityNotFound(entityId: Long) extends Response


  case class ProjectMetaResponse(name: String) extends Response with ProjectMeta

  case class CategoryRoot(root: ActorRef) extends Response

  case class EntityRoot(root: ActorRef) extends Response
}

class Project(meta: ProjectMeta, categoryMeta: CategoryMeta = CategoryMetaDefault("category", Nil)) extends BaseActor {
  
  import Project._
  
  val entityRoot = context.actorOf(Entity(EntityMetaDefault(0, "entity", "", None)), "entity")
  val categoryRoot = context.actorOf(Category(categoryMeta, context.self), "category")
  
  override def receive: Receive = {
    case GetProjectMeta(sendTo) => sendTo getOrElse sender ! ProjectMetaResponse(meta.name)
    case GetCategoryRoot(sendTo) => sendTo getOrElse sender ! CategoryRoot(categoryRoot)
    case GetEntityRoot(sendTo) => sendTo getOrElse sender ! EntityRoot(entityRoot)
    case FindEntity(id, sendTo) =>
      context.actorOf(EntitySearcher(Seq(entityRoot), id, sendTo getOrElse sender))
  }
}

trait ProjectMeta {
  def name: String
}

case class ProjectMetaDefault(name: String) extends ProjectMeta
