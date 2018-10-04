package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.diff.WorkflowSetDiffer.{WorkflowSetDelta, WorkflowSetEquals}
import ru.sber.cb.ap.gusli.actor.core.{Category, CategoryMeta}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

import scala.collection.immutable.HashMap


object CategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import CategoryDiffer._
  import ru.sber.cb.ap.gusli.actor.core.Category._

  private var currProject: Option[ActorRef] = None
  private var currentMeta: Option[CategoryMeta] = None
  private var prevMeta: Option[CategoryMeta] = None
  private var currentSet: Option[Set[ActorRef]] = None
  private var prevSet: Option[Set[ActorRef]] = None
  //todo: combine next two properties
  private var delta: Option[Set[ActorRef]] = None
  private var workflowAlreadyCompared = false

  private var currentCatSet: Option[Set[ActorRef]] = None
  private var prevCatSet: Option[Set[ActorRef]] = None

  private var currMap = HashMap.empty[String, ActorRef]
  private var prevMap = HashMap.empty[String, ActorRef]

  private var catDelta: Set[ActorRef] = Set.empty
  private var differs: Set[ActorRef] = Set.empty

  override def preStart(): Unit = {
    currentCat ! GetProject()

    currentCat ! GetCategoryMeta()
    prevCat ! GetCategoryMeta()

    currentCat ! GetWorkflows()
    prevCat ! GetWorkflows()

    currentCat ! GetSubcategories()
    prevCat ! GetSubcategories()
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
    case WorkflowSet(l) =>
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
    case SubcategorySet(set) =>
      val cat = sender()
      if (cat == currentCat)
        currentCatSet = Some(set)
      else if (cat == prevSet)
        prevCatSet = Some(set)
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

      for (c <- set)
        c ! GetCategoryMeta()

    case CategoryMetaResponse(m) =>
      val cat = sender()
      if (currentSet.get.contains(cat)) {
        currMap += m.name -> cat
        currentSet = Some(currentSet.get - cat)
      }
      else if (prevSet.get.contains(cat)) {
        prevMap += m.name -> cat
        prevSet = Some(prevSet.get - cat)
      }
      else throw new RuntimeException(s"Unexpectable sender $sender")

      if (currentSet.isEmpty && prevSet.isEmpty)
        for ((n, curr) <- currMap)
          prevMap.get(n) match {
            case Some(prev) =>
              differs += context.actorOf(CategoryDiffer(curr, prev, self))
            case None =>
              catDelta += curr
          }
  }

  def checkFinish() = {
    if (currentCatSet.isDefined && prevCatSet.isDefined) {
      val categoryAlreadyCompared = differs.isEmpty && currentCatSet.get.isEmpty && prevCatSet.get.isEmpty
      if (workflowAlreadyCompared && categoryAlreadyCompared) {
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
}
