package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Workflow.WorkflowMetaResponse

class WorkflowWriter(path:Path, categoryMeta:CategoryMeta) extends BaseActor{
  override def receive: Receive = {
    case WorkflowMetaResponse(workflowMeta) =>
      val workflowFolderPath = MetaToHDD.writeWorkflowMetaToPath(workflowMeta, path, categoryMeta)
  }
}

object WorkflowWriter {
  def apply(path:Path, parentMeta:CategoryMeta): Props = Props(new WorkflowWriter(path, parentMeta))
}
