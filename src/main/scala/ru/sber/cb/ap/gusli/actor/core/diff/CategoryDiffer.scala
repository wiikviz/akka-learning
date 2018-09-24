package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.{Category, CategoryMeta}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object CategoryDiffer {
  def apply(diffProject: ActorRef, currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new CategoryDiffer(diffProject, currentCat, prevCat, receiver))

  case class CategoryEquals(currentCat: ActorRef, prevCat: ActorRef) extends Response

  case class CategoryDelta(deltaCat: ActorRef) extends Response

}

class CategoryDiffer(diffProject: ActorRef, currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import CategoryDiffer._
  import ru.sber.cb.ap.gusli.actor.core.Category._

  var currentMeta: Option[CategoryMeta] = None
  var prevMeta: Option[CategoryMeta] = None

  override def preStart(): Unit = {
    currentCat ! GetCategoryMeta()
    prevCat ! GetCategoryMeta()
  }


  override def receive: Receive = {
    case CategoryMetaResponse(m) =>
      if (sender() == currentCat) currentMeta = Some(m)
      else if (sender() == prevCat) prevMeta = Some(m)
      else throw new RuntimeException(s"Unknown sender:${sender()}")

      for (curr <- currentMeta; prev <- prevMeta) {
        if (curr == prev)
          receiver ! CategoryEquals(currentCat, prevCat)
        else {
          val diff = context.system.actorOf(Category(curr, diffProject))
          receiver ! CategoryDelta(diff)
        }
        context.stop(self)
      }
  }
}
