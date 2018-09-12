package ru.sber.cb.ap.gusli.actor.core

import akka.actor.Actor.emptyBehavior
import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object Workflow {

  case class GetMeta(replayTo: Option[ActorRef] = None) extends Request

  case class BindEntity(entityId: Long, replayTo: Option[ActorRef] = None) extends Request

  case class BindEntitySuccessful(entityId: Long) extends Response

  case class BindEntityFailedBecauseItNotExists(entityId: Long) extends Response

  case class WorkflowMetaResponse(name: String, sqlFile: String) extends Response with WorkflowMeta

}

case class Workflow(meta: WorkflowMeta) extends BaseActor {
  override def receive: Receive = emptyBehavior
}

trait WorkflowMeta {
  def name: String

  def sqlFile: String
}

case class WorkflowMetaDefault(name: String, sqlFile: String) extends WorkflowMeta