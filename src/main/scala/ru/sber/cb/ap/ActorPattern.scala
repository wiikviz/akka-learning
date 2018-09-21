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
  val actorName = "CategoryPathResolver"
  val requestMessage1 = "ResolvePath"
  val responseMessage1 = "PathResolved"
  val pattern = s"""
    import akka.actor.{ActorRef, Props}
    import ru.sber.cb.ap.PACKAGE_HERE.$actorName.{$requestMessage1, $responseMessage1}
    import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}
    
    object $actorName {
      def apply(meta: ${actorName}Meta): Props = Props(new $actorName(meta))
    
      case class $requestMessage1(replyTo: Option[ActorRef] = None) extends Request
    
      case class $responseMessage1(replyTo: Option[ActorRef] = None) extends Response
    }
    
    class $actorName(meta: ${actorName}Meta) extends BaseActor {
      override def receive: Receive = {
        case $requestMessage1(replyTo) => replyTo.getOrElse(sender) ! $responseMessage1()
      }
    }
    
    trait ${actorName}Meta {}
    
    case class ${actorName}MetaDefault() extends ${actorName}Meta
    """
  
  println(pattern)
}