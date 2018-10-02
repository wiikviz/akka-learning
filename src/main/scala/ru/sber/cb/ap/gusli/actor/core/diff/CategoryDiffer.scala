package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.diff.WorkflowSetDiffer.{WorkflowSetDelta, WorkflowSetEquals}
import ru.sber.cb.ap.gusli.actor.core.{Category, CategoryMeta}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object CategoryDiffer {
  def apply(diffProject: ActorRef, currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import CategoryDiffer._
  import ru.sber.cb.ap.gusli.actor.core.Category._

  var currProject: Option[ActorRef] = None
  var currentMeta: Option[CategoryMeta] = None
  var prevMeta: Option[CategoryMeta] = None
  var currentSet: Option[Set[ActorRef]] = None
  var prevSet: Option[Set[ActorRef]] = None
  //todo: combine next two properties
  var delta: Option[Set[ActorRef]] = None
  var workflowAlreadyCompared = false

  override def preStart(): Unit = {
    currentCat ! GetProject()

    currentCat ! GetCategoryMeta()
    prevCat ! GetCategoryMeta()

    currentCat ! ListWorkflow()
    prevCat ! ListWorkflow()
  }


  override def receive: Receive = {
    case ProjectResponse(p) =>
      currProject = Some(p)
      checkFinish()
    case CategoryMetaResponse(m) =>
      if (sender() == currentCat) currentMeta = Some(m)
      else if (sender() == prevCat) prevMeta = Some(m)
      else throw new RuntimeException(s"Unknown sender:${sender()}")

      checkFinish()
    case WorkflowList(l) =>
      if (sender() == currentCat) currentSet = Some(l)
      else if (sender() == prevCat) prevSet = Some(l)

      for (curr <- currentSet; prev <- prevSet)
        context.actorOf(WorkflowSetDiffer(curr, prev, self))

      checkFinish()
    case WorkflowSetDelta(d) =>
      delta = Some(d)
      workflowAlreadyCompared = true
      checkFinish()
    case WorkflowSetEquals(_, _) =>
      workflowAlreadyCompared = true
      checkFinish()
  }

  def checkFinish() = {
    if (workflowAlreadyCompared) {
      for (curr <- currentMeta; prev <- prevMeta; project <- currProject) {
        if (curr == prev)
          delta match {
            case None => receiver ! CategoryEquals(currentCat, prevCat)
            case Some(d) =>
              val diff = context.system.actorOf(Category(curr, project))
              diff ! AddWorkflows(d)
              receiver ! CategoryDelta(diff)
          }
        else {
          val diff = context.system.actorOf(Category(curr, project))
          delta match {
            case Some(d) =>
              diff ! AddWorkflows(d)
            case _ => log.debug("delta is empty")
          }

          receiver ! CategoryDelta(diff)
        }
      }
      context.stop(self)
    }
  }
}
