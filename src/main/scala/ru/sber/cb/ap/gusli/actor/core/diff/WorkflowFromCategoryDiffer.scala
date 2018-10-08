package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{GetSubcategories, GetWorkflows, SubcategorySet, WorkflowSet}
import ru.sber.cb.ap.gusli.actor.core.diff.WorkflowSetDiffer.{WorkflowSetDelta, WorkflowSetEquals}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object WorkflowFromCategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props =
    Props(new WorkflowFromCategoryDiffer(currentCat, prevCat, receiver))


  abstract class WorkflowFromCategoryResponse extends Response

  case class WorkflowFromCategoryDelta(wfDelta: Set[ActorRef]) extends WorkflowFromCategoryResponse

  case class WorkflowFromCategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends WorkflowFromCategoryResponse

}

class WorkflowFromCategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import WorkflowFromCategoryDiffer._

  var currentWfs: Option[Set[ActorRef]] = None
  var prevWfs: Option[Set[ActorRef]] = None

  override def preStart(): Unit = {
    currentCat ! GetWorkflows()
    prevCat ! GetWorkflows()
  }

  override def receive: Receive = {
    case WorkflowSet(set) =>
      val cat = sender()
      if (cat == currentCat)
        currentWfs = Some(set)
      else if (cat == prevCat)
        prevWfs = Some(set)
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

      for (c <- currentWfs; p <- prevWfs) {
        context.actorOf(WorkflowSetDiffer(c, p, self))
      }

    case WorkflowSetDelta(d) =>
      receiver ! WorkflowFromCategoryDelta(d)
      context.stop(self)
    case WorkflowSetEquals(_, _) =>
      receiver ! WorkflowFromCategoryEquals(currentCat, prevCat)
      context.stop(self)
  }
}
