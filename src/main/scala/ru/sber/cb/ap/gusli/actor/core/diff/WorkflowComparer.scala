package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowComparer {
  case class CompareWorkflow(wf1: ActorRef, wf2: ActorRef, replyTo: Option[ActorRef] = None) extends Request

  case class WorkflowEquals(wf1: ActorRef, wf2: ActorRef) extends Response

  case class WorkflowNotEquals(wf1: ActorRef, wf2: ActorRef) extends Response

}

class WorkflowComparer extends BaseActor {
  override def receive: Receive = ???
}
