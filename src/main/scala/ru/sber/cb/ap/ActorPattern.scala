package ru.sber.cb.ap

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.ActorPattern.{RequestMessage, ResponseMessage}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object ActorPattern {
  def apply(meta: ActorPattenMeta): Props = Props(new ActorPattern(meta))
  
  case class RequestMessage(replyTo: Option[ActorRef] = None) extends Request
  
  case class ResponseMessage(replyTo: Option[ActorRef] = None) extends Response
}

class ActorPattern(meta: ActorPattenMeta) extends BaseActor {
  override def receive: Receive = {
    case RequestMessage(replyTo) => replyTo.getOrElse(sender) ! ResponseMessage()
  }
}

trait ActorPattenMeta {}

case class ActorPattenMetaDefault()
