package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.EntityMetaDefault
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, EntityNotFound}

object EntitySearcher {
  def apply(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef): Props = Props(new EntitySearcher(entityRefs, entityId, replyTo))
}

class EntitySearcher(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef) extends Actor with ActorLogging {
  val childrenCount = entityRefs.size
  var childrenMetaResponses = 0
  var subChildrenNotFoundResponses = 0

  override def preStart(): Unit = {
    log.info("EntitySearcher entityId={} replyTo={} entities={}", entityId, replyTo, entityRefs)
    if (entityRefs.isEmpty) {
      val notFound = EntityNotFound(entityId)
      log.debug("{} replyTo={}", notFound, replyTo)
      replyTo ! notFound
    }
    else
      entityRefs.foreach(_ ! GetEntityMeta())
  }

  override def receive: Receive = {
    case EntityMetaResponse(meta@EntityMetaDefault(id, _, _, _)) =>
      childrenMetaResponses += 1
      checkNotFound()
      val entity = sender()
      if (id == entityId) {
        val found = EntityFound(meta, entity)
        log.debug("{} replyTo={}", found, replyTo)
        replyTo ! found
        context.stop(self)
      }
      else
        entity ! GetChildren()
    case ChildrenEntityList(kids) =>
      context.actorOf(EntitySearcher(kids, entityId, self))
    case EntityNotFound(_) =>
      subChildrenNotFoundResponses += 1
      //todo: replace >= to smart logic
      if (subChildrenNotFoundResponses >= childrenCount) {
        replyTo ! EntityNotFound(entityId)
        context.stop(self)
      }
      checkNotFound()
    case r@EntityFound(_, _) =>
      replyTo ! r
      context.stop(self)
  }

  def checkNotFound(): Unit = {
    //todo: replace >= to smart logic
    if (childrenMetaResponses >= childrenCount && subChildrenNotFoundResponses >= childrenCount) {
      replyTo ! EntityNotFound(entityId)
      context.stop(self)
    }
  }
}
