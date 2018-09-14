package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.EntityFound

object EntitySearcher {
  def apply(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef): Props = Props(new EntitySearcher(entityRefs, entityId, replyTo))
}

class EntitySearcher(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = {
    log.info("EntitySearcher entityId={} replyTo={} entities={}", entityId, replyTo, entityRefs)
    entityRefs.foreach(_ ! GetEntityMeta())
  }

  override def receive: Receive = {
    case m@EntityMetaResponse(id, _, _) =>
      val entity = sender()
      if (id == entityId)
        replyTo ! EntityFound(m, entity)
      else
        entity ! GetChildren()
    case ChildrenEntityList(kids)=>
      context.actorOf(EntitySearcher(kids, entityId, self))
  }
}
