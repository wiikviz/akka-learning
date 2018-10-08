package ru.sber.cb.ap.gusli

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}

package object actor {

  sealed trait Message

  trait Request extends Message {
    def replyTo: Option[ActorRef]
  }

  trait Response extends Message

  trait ActorResponse extends Response {
    def actorRef: ActorRef
  }

  trait ActorListResponse extends Response {
    def actorList: Seq[ActorRef]
  }

  trait ActorSetResponse extends Response {
    def actorSet: Set[ActorRef]
  }

  abstract class BaseActor extends Actor with Stash with ActorLogging {
    override def unhandled(message: Any): Unit = {
      log.info(Console.RED + s"unhandled = {} from ${sender}" + Console.RESET, message)

      super.unhandled(message)
    }

    override def preStart(): Unit = {
      log.info("preStart = {}", this)
      super.preStart()
    }
  }

}
