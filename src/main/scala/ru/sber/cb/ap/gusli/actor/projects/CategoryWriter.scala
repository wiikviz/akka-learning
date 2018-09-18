package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.{Files, Path}

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor._
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Project.CategoryRoot
import ru.sber.cb.ap.gusli.actor.core.Workflow.GetWorkflowMeta

class CategoryWriter(path:Path, parentMeta:CategoryMeta) extends BaseActor{

  var createdForThisCatrgoryFolderPath:Path = _
  var meta: CategoryMeta = _

//  val afterFolderCreationReceiveBehavior:PartialFunction[Any,Unit] = {}


  override def receive: Receive = {
    //    case GetCategoryRoot(sendTo) => sendTo getOrElse sender ! CategoryRoot(categoryRoot)

    case CategoryRoot(categoryRootActorRef) =>
      categoryRootActorRef ! GetCategoryMeta(Some(context.self))

    case CategoryMetaResponse(categoryMeta: CategoryMeta) =>
      val createdForThisCatrgoryFolderPath = Files createDirectories path resolve categoryMeta.name.replace("-", "-")
      meta = categoryMeta
      // todo write categoryMeta to file
      //      trait CategoryMeta {
      ////        def name: String
      ////        // Content
      ////        def sql: List[String]
      ////        // Content
      ////        def sqlMap: List[String]
      ////        // Content
      ////        def init: List[String]
      ////
      ////        def user: Option[String]
      ////
      ////        def queue: Option[String]
      ////
      ////        def grenkiVersion: Option[String]
      ////
      ////        def params: Map[String, String]
      //      }
      sender ! ListSubcategory(Some(context.self))
      sender ! ListWorkflow(Some(context.self))
//      context become afterFolderCreationReceiveBehavior

    case SubcategoryList(subcategoryActorRefList) =>
      for (subcategory <- subcategoryActorRefList){
        val categoryWriterActorRef = context actorOf CategoryWriter(createdForThisCatrgoryFolderPath,meta)
        subcategory ! GetCategoryMeta(Some(categoryWriterActorRef))
      }

    case WorkflowList(workflowActorRefList) =>
      for(workflow <- workflowActorRefList){
        val workflowWriterActorRef = context actorOf WorkflowWriter(createdForThisCatrgoryFolderPath,meta)
        workflow ! GetWorkflowMeta(Some(workflowWriterActorRef))
      }

  }
}

object CategoryWriter {
  def apply(path:Path, parentMeta:CategoryMeta): Props = Props(new CategoryWriter(path, parentMeta))
}
