package ru.sber.cb.ap.gusli.actor.core.diff

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

object ProjectDiffer {
  def apply(currentProject: ActorRef, prevProject: ActorRef, receiver: ActorRef): Props = Props(new ProjectDiffer(currentProject, prevProject, receiver))

  case class CategoryEquals(currentProject: ActorRef, prevProject: ActorRef) extends Response

  case class CategoryDelta(deltaProject: ActorRef) extends Response

}


class ProjectDiffer(currentProject: ActorRef, prevProject: ActorRef, receiver: ActorRef) extends BaseActor {

  import ru.sber.cb.ap.gusli.actor.core.Category._
  import ru.sber.cb.ap.gusli.actor.core.Project._

  var currentRootCat: ActorRef = _
  var prevRootCat: ActorRef = _
  var currentSubcats: Option[Set[ActorRef]] = None
  var prevSubcats: Option[Set[ActorRef]] = None

  override def preStart(): Unit = {
    currentProject ! GetCategoryRoot()
    prevProject ! GetCategoryRoot()
  }


  override def receive: Receive = {
    case CategoryRoot(root) =>
      if (sender == currentProject)
        currentRootCat = root
      else if (sender == prevProject)
        prevRootCat = root
      else throw new RuntimeException(s"Unexpectable sender $sender")

      root ! GetSubcategories()

    case SubcategorySet(subs) =>
      if (sender == currentRootCat)
        currentSubcats = Some(subs)
      else if (sender == prevRootCat)
        prevSubcats = Some(subs)
      else throw new RuntimeException(s"Unexpectable sender $sender")

  }

}

