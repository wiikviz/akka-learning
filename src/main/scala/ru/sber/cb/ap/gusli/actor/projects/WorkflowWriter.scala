package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, WorkflowMeta}

class WorkflowWriter(path:Path, categoryMeta:CategoryMeta) extends BaseActor{

  var thisWfMeta:WorkflowMeta = _

  override def receive: Receive = ???
  //  override def receive: Receive = {
////    case WorkflowMetaResponse(workflowMeta) =>
////      thisWfMeta = workflowMeta
////      sender ! GetEntityIndexesSet(Some(context.self))
////    case EntityIndexesSet(bindEntityIndexesSet) =>
////      val workflowFolderPath = MetaToHDD.writeWorkflowMetaToPath(thisWfMeta, path, categoryMeta, bindEntityIndexesSet)
////  }
}

object WorkflowWriter {
  def apply(path:Path, parentMeta:CategoryMeta): Props = Props(new WorkflowWriter(path, parentMeta))
}
