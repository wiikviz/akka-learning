package ru.sber.cb.ap.gusli.actor.core.extractor

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core.WorkflowMeta
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.extractor.EntityIdExtractor.EntityIdExtracted
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

object WorkflowDtoExtractor {
  def apply(wf: ActorRef, receiver: ActorRef): Props = Props(new WorkflowDtoExtractor(wf, receiver))

  case class WorkflowExtracted(dto: WorkflowDto) extends Response

}

class WorkflowDtoExtractor(wf: ActorRef, receiver: ActorRef) extends BaseActor {

  import WorkflowDtoExtractor._

  var meta: Option[WorkflowMeta] = None
  var ids: Option[Set[Long]] = None

  override def preStart(): Unit = {
    context.actorOf(EntityIdExtractor(wf, self))
    wf ! GetWorkflowMeta()
  }

  override def receive: Receive = {
    case WorkflowMetaResponse(m) =>
      meta = Some(m)
      checkFinish()
    case EntityIdExtracted(e) =>
      ids = Some(e)
      checkFinish()
  }

  def checkFinish(): Unit = {
    for (m <- meta; i <- ids) yield {
      receiver ! WorkflowExtracted(WorkflowDto(m, i))
      context.stop(self)
    }
  }
}