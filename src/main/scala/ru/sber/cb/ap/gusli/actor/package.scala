package ru.sber.cb.ap.gusli

import akka.actor.{Actor, ActorLogging, ActorRef, Stash}

package object actor {

  sealed trait Message

  trait Request extends Message {
    def replayTo: Option[ActorRef]
  }

  trait Response extends Message

  trait ActorResponse extends Response {
    def actorRef:ActorRef
  }
  trait ActorListResponse extends Response {
    def actorList:Seq[ActorRef]
  }

  abstract class BaseActor extends Actor with Stash with ActorLogging {
    override def unhandled(message: Any): Unit = {
      log.info(Console.RED + "unhandled={}"+ Console.RESET, message)

      super.unhandled(message)
    }

    override def preStart(): Unit = {
      log.info("preStart={}", this)
      super.preStart()
    }
  }
}
