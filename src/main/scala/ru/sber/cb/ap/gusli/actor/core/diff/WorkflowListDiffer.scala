package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core.comparer.WorkflowComparer
import ru.sber.cb.ap.gusli.actor.core.comparer.WorkflowComparer.{WorkflowEquals, WorkflowNotEquals}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

import scala.collection.immutable.HashMap

object WorkflowListDiffer {
  def apply(current: Set[ActorRef], prev: Set[ActorRef], receiver: ActorRef): Props =
    Props(new WorkflowListDiffer(current, prev, receiver))

  case class WorkflowListDelta(delta: Set[ActorRef]) extends Response

  case class WorkflowListEquals(current: Set[ActorRef], prev: Set[ActorRef]) extends Response

}

class WorkflowListDiffer(current: Set[ActorRef], prev: Set[ActorRef], receiver: ActorRef) extends BaseActor {

  import WorkflowListDiffer._

  var currSize = current.size
  var prevSize = prev.size
  var checkCompare: Long = 0

  private var currMap = HashMap.empty[String, ActorRef]
  private var prevMap = HashMap.empty[String, ActorRef]

  private var delta: Set[ActorRef] = Set.empty

  override def preStart(): Unit = {
    for (w <- current)
      w ! GetWorkflowMeta()

    for (w <- prev)
      w ! GetWorkflowMeta()
  }

  override def receive: Receive = {
    case WorkflowMetaResponse(m) =>
      val wf = sender()
      if (current.contains(wf)) {
        currMap = currMap + (m.name -> wf)
        currSize -= 1
      }
      else if (prev.contains(wf)) {
        prevMap = prevMap + (m.name -> wf)
        prevSize -= 1
      }
      else throw new RuntimeException(s"Unknowns sender=$wf")

      if (currSize == 0 && prevSize == 0) {
        for ((name, wf) <- currMap) {
          if (prevMap.contains(name)) {
            checkCompare += 1
            context.actorOf(WorkflowComparer(wf, prevMap(name), self))
          }
          else if (!prevMap.contains(name)) {
            delta += wf

            if (delta.size == current.size) {
              receiver ! WorkflowListDelta(delta)
              context.stop(self)
            }
          }
        }
      }

    case WorkflowEquals(_, _) =>
      checkCompare -= 1
      checkCompareFinished()
    case WorkflowNotEquals(currWf, _) =>
      checkCompare -= 1
      delta += currWf
      checkCompareFinished()
  }

  def checkCompareFinished(): Unit = {
    if (checkCompare == 0) {
      if (delta.isEmpty)
        receiver ! WorkflowListEquals(current, prev)
      else
        receiver ! WorkflowListDelta(delta)

      context.stop(self)
    }
  }
}
