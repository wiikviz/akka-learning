package ru.sber.cb.ap.gusli.actor.pattern

import akka.actor.{Actor, ActorLogging, ActorRef}

class ReplyOnceActor(replyTo: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("ReplyOnceActor replyTo={}", replyTo)

  override def receive: Receive = {
    case msg =>
      replyTo ! msg
      context.stop(self)
  }
}

