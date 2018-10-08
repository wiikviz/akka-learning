package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer.{CategoryDelta, CategoryEquals}
import ru.sber.cb.ap.gusli.actor.core.diff.CategoryMetaDiffer.{AbstractCategoryMetaResponse, CategoryMetaDelta, CategoryMetaEquals}
import ru.sber.cb.ap.gusli.actor.core.diff.WorkflowFromCategoryDiffer.{WorkflowFromCategoryDelta, WorkflowFromCategoryEquals, WorkflowFromCategoryResponse}
import ru.sber.cb.ap.gusli.actor.core.{Category, CategoryMeta}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

import scala.collection.immutable.HashMap


object CategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {
  private var currProject: Option[ActorRef] = None
  private var currentMeta: Option[CategoryMeta] = None
  private var categoryMetaResponse: Option[AbstractCategoryMetaResponse] = None
  private var workflowFromCategory: Option[WorkflowFromCategoryResponse] = None

  private var deltaCat: Option[ActorRef] = None


  override def preStart(): Unit = {
    currentCat ! GetProject()
    currentCat ! GetCategoryMeta()
    context.actorOf(CategoryMetaDiffer(currentCat, prevCat, self))
    context.actorOf(WorkflowFromCategoryDiffer(currentCat, prevCat, self))

    currentCat ! GetSubcategories()
    prevCat ! GetSubcategories()
  }

  override def receive: Receive = {
    case ProjectResponse(p) =>
      currProject = Some(p)
      checkFinish()

    case CategoryMetaResponse(m) =>
      val cat = sender()
      if (cat == currentCat)
        currentMeta = Some(m)
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

      checkFinish()

    case r: AbstractCategoryMetaResponse =>
      categoryMetaResponse = Some(r)
      checkFinish()

    case r: WorkflowFromCategoryResponse =>
      workflowFromCategory = Some(r)
      checkFinish()

  }


  def checkFinish(): Unit = {
    for (project <- currProject; resp: AbstractCategoryMetaResponse <- categoryMetaResponse; curMeta <- currentMeta; wfDelta <- workflowFromCategory) {
      if (deltaCat.isEmpty)
        resp match {
          case CategoryMetaEquals(_, _) =>
            deltaCat = Some(context.system.actorOf(Category(curMeta, project)))

          case CategoryMetaDelta(delta) =>
            deltaCat = Some(context.system.actorOf(Category(delta, project)))
        }

      for (dc <- deltaCat)
        wfDelta match {
          case WorkflowFromCategoryDelta(wd) =>
            dc ! AddWorkflows(wd)
            receiver ! CategoryDelta(dc)
            context.stop(self)
          case WorkflowFromCategoryEquals(_, _) =>
            resp match {
              case CategoryMetaEquals(_, _) =>
                receiver ! CategoryEquals(currentCat, prevCat)

              case CategoryMetaDelta(_) =>
                receiver ! CategoryDelta(dc)
            }

            context.stop(self)
        }
    }
  }
}
