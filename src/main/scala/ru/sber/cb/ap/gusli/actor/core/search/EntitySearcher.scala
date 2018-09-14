package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, EntityNotFound}

object EntitySearcher {
  def apply(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef): Props = Props(new EntitySearcher(entityRefs, entityId, replyTo))
}

class EntitySearcher(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef) extends Actor with ActorLogging {
  val entityRefsSize = entityRefs.size * 2
  var count = 0

  override def preStart(): Unit = {
    log.info("EntitySearcher entityId={} replyTo={} entities={}", entityId, replyTo, entityRefs)
    if (entityRefs.isEmpty)
      replyTo ! EntityNotFound(entityId)
    else
      entityRefs.foreach(_ ! GetEntityMeta())
  }

  override def receive: Receive = {
    case m@EntityMetaResponse(id, _, _) =>
      checkNotFound()
      val entity = sender()
      if (id == entityId)
        replyTo ! EntityFound(m, entity)
      else
        entity ! GetChildren()
    case ChildrenEntityList(kids) =>
      checkNotFound()
      context.actorOf(EntitySearcher(kids, entityId, self))
  }

  def checkNotFound(): Unit = {
    count = count + 1
    log.info("count={}", count)
    if (entityRefsSize == count) {
      replyTo ! EntityNotFound(entityId)
      context.stop(self)
    }
  }
}
