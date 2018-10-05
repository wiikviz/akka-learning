package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object CategoryDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import ru.sber.cb.ap.gusli.actor.core.Category._

  private var prevMeta: Option[CategoryMeta] = None
  private var currMeta: Option[CategoryMeta] = None
  private var currentSubcats: Option[Set[ActorRef]] = None
  private var prevSubcats: Option[Set[ActorRef]] = None

  override def preStart(): Unit = {
    currentCat ! GetCategoryMeta()
    prevCat ! GetCategoryMeta()

    currentCat ! GetSubcategories()
    prevCat ! GetSubcategories()
  }


  override def receive: Receive = {

  }

  def checkFinish() = {
  }
}
