package ru.sber.cb.ap.gusli.actor.core.comparer

import akka.actor.Actor.emptyBehavior
import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

object WorkflowComparer {
  def apply(wf1: ActorRef, wf2: ActorRef, receiver: ActorRef): Props = Props(new WorkflowComparer(wf1, wf2, receiver))

  case class WorkflowEquals(wf1: ActorRef, wf2: ActorRef) extends Response

  case class WorkflowNotEquals(wf1: ActorRef, wf2: ActorRef) extends Response

}

class WorkflowComparer(wf1: ActorRef, wf2: ActorRef, receiver: ActorRef) extends BaseActor {
  override def receive: Receive = emptyBehavior
}
