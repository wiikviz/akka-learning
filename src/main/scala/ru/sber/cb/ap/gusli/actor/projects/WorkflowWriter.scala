package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.{Files, Path}

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.core.Workflow.WorkflowMetaResponse

class WorkflowWriter(path:Path, categoryMeta:CategoryMeta) extends BaseActor{
  override def receive: Receive = {
    case WorkflowMetaResponse(workflowMeta) =>
      val workflowFolderPath = Files createDirectories path resolve workflowMeta.name.replace("-","-")
    // todo write workflowMeta to file


  }
}

object WorkflowWriter {
  def apply(path:Path, parentMeta:CategoryMeta): Props = Props(new WorkflowWriter(path, parentMeta))
}
