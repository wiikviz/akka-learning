package ru.sber.cb.ap

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.ActorPattern.{RequestMessage, ResponseMessage}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object ActorPattern {
  def apply(meta: ActorPatternMeta): Props = Props(new ActorPattern(meta))
  
  case class RequestMessage(replyTo: Option[ActorRef] = None) extends Request
  
  case class ResponseMessage(replyTo: Option[ActorRef] = None) extends Response
}

class ActorPattern(meta: ActorPatternMeta) extends BaseActor {
  override def receive: Receive = {
    case RequestMessage(replyTo) => replyTo.getOrElse(sender) ! ResponseMessage()
  }
}

trait ActorPatternMeta {}

case class ActorPatternMetaDefault()


object CreateActorByPattern extends App {
  val actorName = "WorkflowCreatorByFolder"
  val objectRequestMessages = "ReadFolder" :: Nil
  val objectResponseMessages = "WorkflowRead" :: Nil
  val receiveMessages = "ReadFolder" :: "CategoryMetaResponse" :: "WorkflowCreated" :: Nil
  val pattern = s"""
    import akka.actor.{ActorRef, Props}
    import ru.sber.cb.ap.PACKAGE_HERE.$actorName.{${objectRequestMessages.mkString(", ")}, ${objectResponseMessages.mkString(",")}}
    import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}
    
    object $actorName {
      def apply(meta: ${actorName}Meta): Props = Props(new $actorName(meta))
    
      ${objectRequestMessages.map(m => s"case class $m(replyTo: Option[ActorRef] = None) extends Request").mkString("\n")}
    
      ${objectResponseMessages.map(m => s"case class $m(replyTo: Option[ActorRef] = None) extends Response").mkString("\n")}
    }
    
    class $actorName(meta: ${actorName}Meta) extends BaseActor {
      override def receive: Receive = {
        ${receiveMessages.map(m => s"case $m(replyTo) => ").mkString("\n")}
      }
    }
    
    trait ${actorName}Meta {}
    
    case class ${actorName}MetaDefault() extends ${actorName}Meta
    """
  
  println(pattern)
}