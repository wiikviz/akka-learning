package ru.sber.cb.ap.gusli.actor.projects.read.category.create

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddWorkflow, CategoryMetaResponse, GetCategoryMeta, WorkflowCreated}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Workflow.BindEntity
import ru.sber.cb.ap.gusli.actor.projects.read.MetaToChildInheritor
import ru.sber.cb.ap.gusli.actor.projects.read.category.ProjectMetaMaker
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.WorkflowCreatorByFolder.{ReadWorkflowFolder, WorkflowFolderRead}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.{WorkflowOptionDto, YamlFileMapper}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowCreatorByFolder {
  def apply(meta: WorkflowCreatorByFolderMeta): Props = Props(new WorkflowCreatorByFolder(meta))
  
  case class ReadWorkflowFolder(replyTo: Option[ActorRef] = None) extends Request
  
  case class WorkflowFolderRead() extends Response
  
}

class WorkflowCreatorByFolder(meta: WorkflowCreatorByFolderMeta) extends BaseActor {
  private val entities = scala.collection.mutable.HashSet[Long]()
  
  override def receive: Receive = {
    case ReadWorkflowFolder(replyTo) => this.meta.category ! GetCategoryMeta()
    case CategoryMetaResponse(meta) =>
      tryCreateWorkflow(meta)
    case WorkflowCreated(wf) =>
      entities.foreach(wf ! BindEntity(_))
      context.parent ! WorkflowFolderRead()
      context.stop(self)
  }
  
  private def tryCreateWorkflow(meta: CategoryMeta): Unit = {
    val wfMetaTemp = extractMetaFileFields(meta)
    if (wfMetaTemp.isEmpty) Left("Meta File not found in " + this.meta.path)
    else {
      //TODO: Фильтр отрицательных сущностей
      entities ++= MetaToChildInheritor.inheritSetOfLong(meta.entities, wfMetaTemp.get.entities)
      this.meta.category ! AddWorkflow(inheritMeta(meta, wfMetaTemp))
    }
  }
  
  private def extractMetaFileFields(meta: CategoryMeta) = YamlFileMapper.readToWorkflowOptionDto(this.meta.path)
  
  private def inheritMeta(meta: CategoryMeta, wfMetaTemp: Option[WorkflowOptionDto]) =
    ProjectMetaMaker.workflowNonEmptyMeta(meta, wfMetaTemp.get)
  
}

trait WorkflowCreatorByFolderMeta {
  val path: Path
  val category: ActorRef
}

case class WorkflowCreatorByFolderMetaDefault(path: Path, category: ActorRef) extends WorkflowCreatorByFolderMeta
