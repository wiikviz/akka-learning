package ru.sber.cb.ap.gusli.actor.projects.write.category

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor._
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Project.CategoryRoot
import ru.sber.cb.ap.gusli.actor.core.Workflow.GetWorkflowMeta
import ru.sber.cb.ap.gusli.actor.projects.write.MetaToHDD
import ru.sber.cb.ap.gusli.actor.projects.write.category.WorkflowWriter.WorkflowWrote

object CategoryWriter {
  def apply(path: Path, parentMeta: CategoryMeta): Props = Props(new CategoryWriter(path, parentMeta))
  
  case class Wrote() extends Response
}

class CategoryWriter(parentPath: Path, parentMeta: CategoryMeta) extends BaseActor {
  
  private var thisPath: Path = _
  private var thisMeta: CategoryMeta = _
  
  private[this] var catCount = Int.MaxValue
  private[this] var wfCount = Int.MaxValue
  private[this] var answeredCategoriesCount = 0
  private[this] var answeredWorkflowsCount = 0
  
  override def receive: Receive = {
    
    case CategoryRoot(category) =>
      getMeta(category)
    
    case CategoryMetaResponse(categoryMeta: CategoryMeta) =>
      thisMeta = categoryMeta
      thisPath = MetaToHDD.writeCategoryMetaToPath(thisMeta, parentPath, parentMeta)
      sender ! GetSubcategories()
      sender ! GetWorkflows()
    
    case SubcategorySet(list) =>
      catCount = list.size
      writeCategories(list)
      checkFinish()
      
    case WorkflowSet(list) =>
      wfCount = list.size
      writeWorkflows(list)
      checkFinish()
      
    case WorkflowWrote() =>
      answeredWorkflowsCount += 1
      checkFinish()
      
    case CategoryWriter.Wrote() =>
      answeredCategoriesCount += 1
      checkFinish()
  }
  
  private def writeWorkflows(list: Set[ActorRef]): Unit = {
    for (wf <- list) {
      val workflowWriter = context.actorOf(WorkflowWriter(thisPath, thisMeta))
      wf ! GetWorkflowMeta(Some(workflowWriter))
    }
  }
  
  private def writeCategories(list: Set[ActorRef]): Unit = {
    for (cat <- list) {
      val workflowWriter = context.actorOf(CategoryWriter(thisPath, thisMeta))
      cat ! GetCategoryMeta(Some(workflowWriter))
    }
  }
  
  private def getMeta(category: ActorRef): Unit = {
    category ! GetCategoryMeta()
  }
  
  private def checkFinish(): Unit = if (answeredWorkflowsCount == wfCount && answeredCategoriesCount == catCount)
    finish()
  
  private def finish(): Unit = {
    context.parent ! CategoryWriter.Wrote()
    context.stop(self)
  }
}
