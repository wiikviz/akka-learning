package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{GetProject, ProjectResponse}
import ru.sber.cb.ap.gusli.actor.core.diff.CategoryMetaDiffer.{CategoryMetaDelta, CategoryMetaEquals}
import ru.sber.cb.ap.gusli.actor.core.{Category, CategoryMeta}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object CategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import CategoryDiffer._

  private var currProject: Option[ActorRef] = None

  private var isCategoryMetaCompared = false
  private var metaDelta: Option[CategoryMeta] = None

  private var deltaCat: Option[ActorRef] = None

  override def preStart(): Unit = {
    currentCat ! GetProject()
    context.actorOf(CategoryMetaDiffer(currentCat, prevCat, self))
    context.actorOf(WorkflowFromCategoryDiffer(currentCat, prevCat, self))
  }


  override def receive: Receive = {
    case ProjectResponse(p) =>
      currProject = Some(p)
      checkFinish()

    case CategoryMetaEquals(_, _) =>
      isCategoryMetaCompared = true
      checkFinish()

    case CategoryMetaDelta(d) =>
      metaDelta = Some(d)
      isCategoryMetaCompared = true
      checkFinish()

  }

  def checkFinish(): Unit = {
    for (project <- currProject) {
      if (isCategoryMetaCompared) {
        if (metaDelta.isDefined) {
          deltaCat = Some(context.system.actorOf(Category(metaDelta.get, project)))
          receiver ! CategoryDelta(deltaCat.get)
          context.stop(self)
        }
      }
    }
  }
}
