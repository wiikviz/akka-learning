package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer.{CategoryDelta, CategoryEquals}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

import scala.collection.immutable.HashMap


object CategorySetDiffer {
  def apply(currentSet: Set[ActorRef], prevSet: Set[ActorRef], receiver: ActorRef): Props = Props(new CategorySetDiffer(currentSet, prevSet, receiver))

  case class CategorySetsEquals(currentSet: Set[ActorRef], prevSet: Set[ActorRef]) extends Response

  case class CategorySetDelta(delta: Set[ActorRef]) extends Response

}

class CategorySetDiffer(var currentSet: Set[ActorRef], var prevSet: Set[ActorRef], receiver: ActorRef) extends BaseActor {

  import CategorySetDiffer._
  import ru.sber.cb.ap.gusli.actor.core.Category._

  private var currMap = HashMap.empty[String, ActorRef]
  private var prevMap = HashMap.empty[String, ActorRef]

  private var delta: Set[ActorRef] = Set.empty
  private var differs: Set[ActorRef] = Set.empty

  override def preStart(): Unit = {
    if (currentSet.isEmpty && prevSet.isEmpty) {
      receiver ! CategorySetsEquals(currentSet, prevSet)
      context.stop(self)
    }
    else {
      for (c <- currentSet)
        c ! GetCategoryMeta()

      for (c <- prevSet)
        c ! GetCategoryMeta()
    }
  }


  override def receive: Receive = {
    case CategoryMetaResponse(m) =>
      val cat = sender()
      if (currentSet.contains(cat)) {
        currMap += m.name -> cat
        currentSet -= cat
      }
      else if (prevSet.contains(cat)) {
        prevMap += m.name -> cat
        prevSet -= cat
      }
      else throw new RuntimeException(s"Unexpectable sender $sender")

      if (currentSet.isEmpty && prevSet.isEmpty)
        for ((n, curr) <- currMap)
          prevMap.get(n) match {
            case Some(prev) =>
              differs += context.actorOf(CategoryDiffer(curr, prev, self))
            case None =>
              delta += curr
          }

    case CategoryEquals(curr, prev) =>
      val diff = sender
      differs -= diff
      checkFinish()
    case CategoryDelta(d) =>
      val diff = sender
      differs -= diff
      delta += d
      checkFinish()
  }

  def checkFinish() = {
    if (differs.isEmpty) {
      if (delta.isEmpty)
        receiver ! CategorySetsEquals(currMap.values.toSet, prevMap.values.toSet)
      else
        receiver ! CategorySetDelta(delta)

      context.stop(self)
    }
  }
}
