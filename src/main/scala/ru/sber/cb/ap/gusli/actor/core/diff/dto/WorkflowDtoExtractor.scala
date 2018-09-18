package ru.sber.cb.ap.gusli.actor.core.diff.dto

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object WorkflowDtoExtractor {
  def apply(): Props = Props(new WorkflowDtoExtractor())

  case class Extract(wf: ActorRef, replyTo: Option[ActorRef] = None) extends Request

  case class WorkflowExtracted(dto: WorkflowDto) extends Response

}

class WorkflowDtoExtractor extends BaseActor {
  type Workflow = ActorRef
  type Requester = ActorRef

  import WorkflowDtoExtractor._

  var awaitExtract: Map[Workflow, Requester] = Map.empty

  override def receive: Receive = {
    case Extract(wf, sendTo) =>
      val replyTo = sendTo getOrElse sender
      awaitExtract = awaitExtract + (wf -> replyTo)
      wf ! GetWorkflowMeta()
    case WorkflowMetaResponse(m) =>
      //todo: replace for unapply
      val dto = WorkflowDto(m.name, m.sql, m.sqlMap, m.init, m.user, m.queue, m.grenkiVersion, m.params, m.stats, Set.empty)

      val wf = sender()
      awaitExtract(wf) ! WorkflowExtracted(dto)
      awaitExtract = awaitExtract - wf
  }
}
