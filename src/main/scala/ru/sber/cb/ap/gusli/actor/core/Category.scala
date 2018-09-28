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

  case class CategoryMetaResponse(meta: CategoryMeta) extends Response

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
      replyTo ! CategoryMetaResponse(meta)

    case mess@AddSubcategory(m, sendTo) =>
      log.info("{}", mess)
      val replyTo = sendTo.getOrElse(sender())
      subcategoresRegistry.get(m.name) match {
        case Some(subcat) => replyTo ! subcat
        case None =>
          val subcat = context.actorOf(Category(m, project))
          subcategoresRegistry = subcategoresRegistry + (m.name -> subcat)
          replyTo ! SubcategoryCreated(subcat)
      }
    case ListSubcategory(sendTo) =>
      sendTo.getOrElse(sender)  ! SubcategoryList(subcategoresRegistry.values.toSeq)
      
    case mess@AddWorkflow(m, sendTo) =>
      log.info("{}", mess)
      val replyTo = sendTo.getOrElse(sender)
      val fromRegistry = workflowsRegistry get m.name
      if(fromRegistry.isEmpty){
        val newWorkflow = context.actorOf(Workflow(m, project))
        workflowsRegistry = workflowsRegistry + (m.name -> newWorkflow)
        replyTo ! WorkflowCreated(newWorkflow)
      } else replyTo ! WorkflowCreated(fromRegistry.get)

    case ListWorkflow(sendTo) =>
      sendTo.getOrElse(sender)  ! WorkflowList(workflowsRegistry.values.toSeq)
      
  }
}

trait CategoryMeta {
  def name: String

  // Content
  def sqlMap: Map[String, String]
  // Content
  def init: Map[String, String]
  
  def user: Option[String]
  
  def queue: Option[String]
  
  def grenkiVersion: Option[String]
  
  def params: Map[String, String]
  
  def stats: Set[Long]
  
  def entities: Set[Long]
}

case class CategoryMetaDefault(name: String,
                               sqlMap: Map[String, String] = Map.empty,
                               init: Map[String, String] = Map.empty,
                               user: Option[String] = None,
                               queue: Option[String] = None,
                               grenkiVersion: Option[String] = None,
                               params: Map[String, String] = Map.empty,
                               stats: Set[Long] = Set.empty,
                               entities: Set[Long] = Set.empty
) extends CategoryMeta