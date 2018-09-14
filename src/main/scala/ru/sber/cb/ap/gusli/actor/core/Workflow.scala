package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntityFailedBecauseItNotExists, GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object Workflow {
  def apply(meta: WorkflowMeta, project: ActorRef): Props = Props(new Workflow(meta, project))

  case class GetWorkflowMeta(replyTo: Option[ActorRef] = None) extends Request

  case class BindEntity(entityId: Long, replyTo: Option[ActorRef] = None) extends Request

  case class BindEntitySuccessful(entityId: Long) extends Response

  case class BindEntityFailedBecauseItNotExists(entityId: Long) extends Response

  case class WorkflowMetaResponse(name: String, sqlFile: String) extends Response with WorkflowMeta

}

class Workflow(meta: WorkflowMeta, project: ActorRef) extends BaseActor {
  override def receive: Receive = {
    case GetWorkflowMeta(sendTo) => sendTo getOrElse sender ! WorkflowMetaResponse(meta.name, meta.sqlFile)
    case BindEntity(id,sendTo) => sendTo getOrElse sender ! BindEntityFailedBecauseItNotExists(id)
  }
}

trait WorkflowMeta {
  def name: String

  def sqlFile: String
}

case class WorkflowMetaDefault(name: String, sqlFile: String) extends WorkflowMeta