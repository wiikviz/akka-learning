package ru.sber.cb.ap.gusli.actor.core

import akka.actor.Actor.emptyBehavior
import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.{ActorListResponse, ActorResponse, BaseActor, Request}

object Entity {
  case class AddChildEntity(meta: EntityMeta, replayTo: Option[ActorRef] = None) extends Request

  case class GetChildren(replayTo: Option[ActorRef] = None) extends Request

  case class EntityCreated(actorRef: ActorRef) extends ActorResponse

  case class ChildrenEntityList(actorList: Seq[ActorRef]) extends ActorListResponse
}

case class Entity(meta: EntityMeta) extends BaseActor {
  override def receive: Receive = emptyBehavior
}

trait EntityMeta {
  def id: Long
  def name: String
  def path: String
}