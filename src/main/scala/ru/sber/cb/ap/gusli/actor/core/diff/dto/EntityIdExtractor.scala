package ru.sber.cb.ap.gusli.actor.core.diff.dto

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{EntityList, GetEntityList}
import ru.sber.cb.ap.gusli.actor.core.diff.dto.EntityIdExtractor.EntityIdExtracted
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

object EntityIdExtractor {
  def apply(wf: ActorRef, receiver: ActorRef): Props = Props(new EntityIdExtractor(wf, receiver))

  case class EntityIdExtracted(ids: Seq[Long]) extends Response

}

class EntityIdExtractor(wf: ActorRef, receiver: ActorRef) extends BaseActor {
  var countDown: Long = _
  var entityIds: List[Long] = List.empty

  override def preStart(): Unit = {
    wf ! GetEntityList()
  }

  override def receive: Receive = {
    case EntityList(entities) =>
      countDown = entities.size
      entities.foreach(_ ! GetEntityMeta())
    case EntityMetaResponse(m) =>
      entityIds = m.id :: entityIds
      countDown -= 1
      if (countDown == 0) {
        receiver ! EntityIdExtracted(entityIds)
        context.stop(self)
      }
  }
}