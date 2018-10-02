package ru.sber.cb.ap.gusli.actor.projects.write.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor._
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Project.CategoryRoot
import ru.sber.cb.ap.gusli.actor.core.Workflow.GetWorkflowMeta
import ru.sber.cb.ap.gusli.actor.projects.write.{MetaToHDD, WorkflowWriter}

object CategoryWriter {
  def apply(path: Path, parentMeta: CategoryMeta): Props = Props(new CategoryWriter(path, parentMeta))
}

class CategoryWriter(parentPath: Path, parentMeta: CategoryMeta) extends BaseActor {
  
  private var thisPath: Path = _
  private var thisMeta: CategoryMeta = _
  
  private var wfListGetted = false
  private var catListGetted = false
  
  override def receive: Receive = {
    
    case CategoryRoot(category) =>
      getMeta(category)
    
    case CategoryMetaResponse(categoryMeta: CategoryMeta) =>
      thisMeta = categoryMeta
      thisPath = MetaToHDD.writeCategoryMetaToPath(thisMeta, parentPath, parentMeta)
      sender ! GetSubcategories()
      sender ! GetWorkflows()
    
    case SubcategorySet(list) =>
      catListGetted = true //для чек-финиш
      writeCategories(list)
    
    case WorkflowSet(list) =>
      wfListGetted = true //для чек-финиш
      writeWorkflows(list)
  }
  
  private def writeWorkflows(list: Set[ActorRef]) = {
    for (wf <- list) {
      val workflowWriter = context.actorOf(WorkflowWriter(thisPath, thisMeta))
      wf ! GetWorkflowMeta(Some(workflowWriter))
    }
  }
  
  private def writeCategories(list: Set[ActorRef]) = {
    for (cat <- list) {
      val workflowWriter = context.actorOf(CategoryWriter(thisPath, thisMeta))
      cat ! GetCategoryMeta(Some(workflowWriter))
    }
  }
  
  private def getMeta(category: ActorRef) = {
    category ! GetCategoryMeta()
  }
}
