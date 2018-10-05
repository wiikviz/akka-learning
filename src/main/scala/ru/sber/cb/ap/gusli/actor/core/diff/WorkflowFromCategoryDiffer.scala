package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{GetSubcategories, SubcategorySet}
import ru.sber.cb.ap.gusli.actor.core.diff.WorkflowSetDiffer.{WorkflowSetDelta, WorkflowSetEquals}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object WorkflowFromCategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props =
    Props(new WorkflowFromCategoryDiffer(currentCat, prevCat, receiver))

  case class WorkflowFromCategoryDelta(wfDelta: Set[ActorRef]) extends Response

  case class WorkflowFromCategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

}

class WorkflowFromCategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import WorkflowFromCategoryDiffer._

  var currentSubs: Option[Set[ActorRef]] = None
  var prevSubs: Option[Set[ActorRef]] = None

  override def preStart(): Unit = {
    currentCat ! GetSubcategories()
    prevCat ! GetSubcategories()
  }

  override def receive: Receive = {
    case SubcategorySet(set) =>
      val cat = sender()
      if (cat == currentCat)
        currentSubs = Some(set)
      else if (cat == prevCat)
        prevSubs = Some(set)
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

      for (c <- currentSubs; p <- prevSubs) {
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
