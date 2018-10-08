package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

import scala.collection.immutable.HashMap

object SubCategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new SubCategoryDiffer(currentCat, prevCat, receiver))

  abstract class AbstractSubCategoryResponse extends Response

  case class SubCategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends AbstractSubCategoryResponse

  case class SubCategoryDelta(deltaCats: Set[ActorRef]) extends AbstractSubCategoryResponse

}


class SubCategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import CategoryDiffer._
  import SubCategoryDiffer._
  import ru.sber.cb.ap.gusli.actor.core.Category._

  private var currentSet: Option[Set[ActorRef]] = None
  private var currentMap: HashMap[String, ActorRef] = HashMap.empty[String, ActorRef]
  private var prevSet: Option[Set[ActorRef]] = None
  private var prevMap = HashMap.empty[String, ActorRef]

  private var subCatDelta: Set[ActorRef] = Set.empty
  private var catDiffs: Set[ActorRef] = Set.empty

  override def preStart(): Unit = {
    currentCat ! GetSubcategories()
    prevCat ! GetSubcategories()
  }

  override def receive: Receive = {
    case SubcategorySet(s) =>
      val cat = sender()
      if (cat == currentCat) {
        currentSet = Some(s)
        for (c <- s)
          c ! GetCategoryMeta()
      }
      else if (cat == prevCat) {
        prevSet = Some(s)
        for (c <- s)
          c ! GetCategoryMeta()
      }
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

    case CategoryMetaResponse(m) =>
      val cat = sender()
      if (isCurrentSetContains(cat)) {
        currentSet = Some(currentSet.get - cat)
        currentMap += (m.name -> cat)
      }
      else if (isPrevSetContains(cat)) {
        prevSet = Some(prevSet.get - cat)
        prevMap += (m.name -> cat)
      }
      else
        throw new RuntimeException(s"Unknown sender:${sender()}")

      checkAllSubcategoryExported()

    case CategoryEquals(_, _) =>
      val diff = sender()
      catDiffs -= diff
      checkAllSubcategoriesCompared()
    case CategoryDelta(delta) =>
      val diff = sender()
      catDiffs -= diff
      subCatDelta += delta
      checkAllSubcategoriesCompared()
  }

  def isCurrentSetContains(cat: ActorRef): Boolean =
    currentSet match {
      case None =>
        log.debug("Subcategories from current category is not load yet")
        false
      case Some(set) =>
        if (set.contains(cat)) true
        else false
    }

  def isPrevSetContains(cat: ActorRef): Boolean =
    prevSet match {
      case None =>
        log.debug("Subcategories from previous category is not load yet")
        false
      case Some(set) =>
        if (set.contains(cat)) true
        else false
    }

  def checkAllSubcategoryExported(): Unit = {
    (currentSet, prevSet) match {
      case (None, None) =>
        log.debug("SubCategories not load yet")
      case (Some(currSet), Some(prevSet)) =>
        if (currSet.isEmpty && prevSet.isEmpty) {
          val diffSubsNames = currentMap.keySet diff prevMap.keySet
          for (n <- diffSubsNames) {
            subCatDelta += currentMap(n)
            currentMap -= n
          }

          val eqSubsNames = currentMap.keySet intersect prevMap.keySet
          for (n <- eqSubsNames) {
            val curr = currentMap(n)
            val prev = prevMap(n)
            catDiffs += context.actorOf(CategoryDiffer(curr, prev, self),s"diff-$n")
          }
          if (catDiffs.isEmpty) {
            receiver ! SubCategoryDelta(subCatDelta)
            context.stop(self)
          }

        }
    }
  }

  def checkAllSubcategoriesCompared(): Unit = {
    import ru.sber.cb.ap.gusli.actor.core._
    if (catDiffs.isEmpty)
      subCatDelta match {
        case EmptySet() =>
          receiver ! SubCategoryEquals(currentCat, prevCat)
          context.stop(self)
        case NonEmptySet(diff) =>
          receiver ! SubCategoryDelta(diff)
          context.stop(self)
      }
  }
}
