package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object EntitySearcher {
  def apply(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef): Props = Props(new EntitySearcher(entityRefs, entityId, replyTo))
}

class EntitySearcher(entityRefs: Seq[ActorRef], entityId: Long, replyTo: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = {
    log.info("EntitySearcher entityId={} replyTo={} entities={}", entityId, replyTo, entityRefs)

  }

  override def receive: Receive = {
    case msg =>
      replyTo ! msg
      context.stop(self)
  }
}

