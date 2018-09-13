package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor._

import scala.collection.immutable.HashMap

object Category {
  def apply(meta: CategoryMeta, project: ActorRef): Props = Props(new Category(meta, project))

  case class GetCategoryMeta(replyTo: Option[ActorRef] = None) extends Request

  case class AddSubcategory(meta: CategoryMeta, replyTo: Option[ActorRef] = None) extends Request

  case class ListSubcategory(replyTo: Option[ActorRef] = None) extends Request

  case class AddWorkflow(meta: WorkflowMeta, replyTo: Option[ActorRef] = None) extends Request

  case class ListWorkflow(replyTo: Option[ActorRef] = None) extends Request

  //

  case class CategoryMetaResponse(name: String) extends Response with CategoryMeta

  case class SubcategoryCreated(actorRef: ActorRef) extends ActorResponse

  case class SubcategoryList(actorList: Seq[ActorRef]) extends ActorListResponse

  case class WorkflowCreated(actorRef: ActorRef) extends ActorResponse

  case class WorkflowList(actorList: Seq[ActorRef]) extends ActorListResponse

}

class Category(meta: CategoryMeta, project: ActorRef) extends BaseActor {

  import Category._
  
  private var subcategoresRegistry: HashMap[String, ActorRef] = HashMap.empty[String, ActorRef]
  private var workflowsRegistry: HashMap[String, ActorRef] = HashMap.empty[String, ActorRef]
  
  override def receive: Receive = {
    case GetCategoryMeta(sendTo) =>
      val replyTo = sendTo.getOrElse(sender())
      replyTo ! CategoryMetaResponse(meta.name)

    case m@AddSubcategory(meta, sendTo) =>
      log.info("{}", m)
      val replyTo = sendTo.getOrElse(sender())
      subcategoresRegistry.get(meta.name) match {
        case Some(subcat) => replyTo ! subcat
        case None =>
          val subcat = context.actorOf(Category(meta, project))
          subcategoresRegistry = subcategoresRegistry + (meta.name -> subcat)
          replyTo ! SubcategoryCreated(subcat)
      }
    case ListSubcategory(sendTo) =>
      sendTo getOrElse sender ! SubcategoryList(subcategoresRegistry.values.toSeq)
      
    case m@AddWorkflow(meta, sendTo) =>
      log.info("{}", m)
      val replyTo = sendTo getOrElse sender
      val fromRegisty = workflowsRegistry get meta.name
      if(fromRegisty isEmpty){
        val newWorkflow = context.actorOf(Workflow(meta, project))
        workflowsRegistry = workflowsRegistry + (meta.name -> newWorkflow)
        replyTo ! WorkflowCreated(newWorkflow)
      } else replyTo ! WorkflowCreated(fromRegisty.get)

    case ListWorkflow(sendTo) =>
      sendTo getOrElse sender ! WorkflowList(workflowsRegistry.values.toSeq)
      
  }
}

trait CategoryMeta {
  def name: String
}

case class CategoryMetaDefault(name: String) extends CategoryMeta