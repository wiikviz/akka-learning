package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor._
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Project.CategoryRoot
import ru.sber.cb.ap.gusli.actor.core.Workflow.GetWorkflowMeta

class CategoryWriter(path:Path, parentMeta:CategoryMeta) extends BaseActor{

  var createdForThisCatrgoryFolderPath:Path = _
  var meta: CategoryMeta = _

  override def receive: Receive = {

    case CategoryRoot(categoryRootActorRef) =>
      categoryRootActorRef ! GetCategoryMeta(Some(context.self))

    case CategoryMetaResponse(categoryMeta: CategoryMeta) =>
      createdForThisCatrgoryFolderPath = MetaToHDD.writeCategoryMetaToPath(categoryMeta,path, parentMeta)
      meta = categoryMeta
      sender ! ListSubcategory(Some(context.self))
      sender ! ListWorkflow(Some(context.self))

    case SubcategoryList(subcategoryActorRefList) =>
      for (subcategory <- subcategoryActorRefList){
        val categoryWriterActorRef = context actorOf CategoryWriter(createdForThisCatrgoryFolderPath, meta)
        subcategory ! GetCategoryMeta(Some(categoryWriterActorRef))
      }

    case WorkflowList(workflowActorRefList) =>
      for(workflow <- workflowActorRefList){
        val workflowWriterActorRef = context actorOf WorkflowWriter(createdForThisCatrgoryFolderPath, meta)
        workflow ! GetWorkflowMeta(Some(workflowWriterActorRef))
      }
  }
}

object CategoryWriter {
  def apply(path:Path, parentMeta:CategoryMeta): Props = Props(new CategoryWriter(path, parentMeta))
}
