package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{AddChildEntity, ChildrenEntityList, EntityCreated, GetChildren}
import ru.sber.cb.ap.gusli.actor.{ActorListResponse, ActorResponse, BaseActor, Request}

import scala.collection.immutable.HashMap

object Entity {
  def apply(meta: EntityMeta): Props = Props(new Entity(meta))
  
  case class AddChildEntity(meta: EntityMeta, replyTo: Option[ActorRef] = None) extends Request

  case class GetChildren(replyTo: Option[ActorRef] = None) extends Request

  case class EntityCreated(actorRef: ActorRef) extends ActorResponse

  case class ChildrenEntityList(actorList: Seq[ActorRef]) extends ActorListResponse
}

case class Entity(meta: EntityMeta) extends BaseActor {
  private var subentitesRegistry: HashMap[Long, ActorRef] = HashMap.empty[Long, ActorRef]
  
  override def receive: Receive = {
    case m @ AddChildEntity(meta, sendTo) =>
      log.info("{}", m)
      val replyTo = sendTo getOrElse sender
      val fromRegisty = subentitesRegistry get meta.id
      if(fromRegisty isEmpty){
        val newEntity = context actorOf Entity(meta)
        subentitesRegistry = subentitesRegistry + (meta.id -> newEntity)
        replyTo ! EntityCreated(newEntity)
      } else replyTo ! EntityCreated(fromRegisty.get)
    case GetChildren(sendTo) =>
      sendTo getOrElse sender ! ChildrenEntityList(subentitesRegistry.values.toSeq)
  }
}

trait EntityMeta {
  def id: Long
  def name: String
  def path: String
}