package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer.{SubCategoryDelta, SubCategoryEquals}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}


object SubCategoryMetaDiffer {
  def apply(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef): Props = Props(new SubCategoryMetaDiffer(currentCat, prevCat, receiver))

  abstract class AbstractSubCategoryMetaResponse extends Response

  case class SubCategoryMetaEquals(currentCat: ActorRef, prevCat: ActorRef) extends AbstractSubCategoryMetaResponse

  case class SubCategoryMetaDelta(deltaCats: Set[CategoryMeta]) extends AbstractSubCategoryMetaResponse

}

class SubCategoryMetaDiffer(currentCat: ActorRef, prevCat: ActorRef, receiver: ActorRef) extends BaseActor {

  import SubCategoryMetaDiffer._

  var subCategoryCount = 0
  var metaDelta: Set[CategoryMeta] = Set.empty

  override def preStart(): Unit = {
    context.actorOf(SubCategoryDiffer(currentCat, prevCat, self))
  }

  override def receive: Receive = {
    case SubCategoryEquals(_, _) =>
      receiver ! SubCategoryMetaEquals(currentCat, prevCat)
      context.stop(self)
    case SubCategoryDelta(set) =>
      for (s <- set) {
        s ! GetCategoryMeta()
        subCategoryCount += 1
      }
    case CategoryMetaResponse(m) =>
      metaDelta += m
      subCategoryCount -= 1
      if (subCategoryCount == 0) {
        receiver ! SubCategoryMetaDelta(metaDelta)
      }
  }

}
