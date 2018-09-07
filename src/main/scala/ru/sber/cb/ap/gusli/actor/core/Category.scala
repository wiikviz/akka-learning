package ru.sber.cb.ap.gusli.actor.core

import akka.actor.Actor.emptyBehavior
import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor._

import scala.collection.immutable.HashMap

object Category {
  def apply(meta: CategoryMeta): Props = Props(new Category(meta))

  case class GetCategoryMeta(replayTo: Option[ActorRef] = None) extends Request

  case class AddSubcategory(meta: CategoryMeta, replayTo: Option[ActorRef] = None) extends Request

  case class ListSubcategory(replayTo: Option[ActorRef] = None) extends Request

  case class AddWorkflow(meta: WorkflowMeta, replayTo: Option[ActorRef] = None) extends Request

  case class ListWorkflow(replayTo: Option[ActorRef] = None) extends Request

  //

  case class CategoryMetaResponse(name: String) extends Response with CategoryMeta

  case class SubcategoryCreated(actorRef: ActorRef) extends ActorResponse

  case class SubcategoryList(actorList: Seq[ActorRef]) extends ActorListResponse

  case class WorkflowCreated(actorRef: ActorRef) extends ActorResponse

  case class WorkflowList(actorList: Seq[ActorRef]) extends ActorListResponse

}

class Category(meta: CategoryMeta) extends BaseActor {

  import Category._

  private var registry: HashMap[String, ActorRef] = HashMap.empty[String, ActorRef]

  override def receive: Receive = {
    case GetCategoryMeta(sendTo) =>
      val replayTo = sendTo.getOrElse(sender())
      replayTo ! CategoryMetaResponse(meta.name)

    case m@AddSubcategory(meta, sendTo) =>
      log.info("{}", m)
      val replayTo = sendTo.getOrElse(sender())
      registry.get(meta.name) match {
        case Some(subcat) => replayTo ! subcat
        case None =>
          val subcat = context.actorOf(Category(meta))
          registry = registry + (meta.name -> subcat)
          replayTo ! SubcategoryCreated(subcat)
      }
  }
}

trait CategoryMeta {
  def name: String
}

case class CategoryMetaDefault(name: String) extends CategoryMeta