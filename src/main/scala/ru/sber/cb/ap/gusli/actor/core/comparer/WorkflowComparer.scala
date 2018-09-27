package ru.sber.cb.ap.gusli.actor.core.comparer

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.extractor.WorkflowDtoExtractor
import ru.sber.cb.ap.gusli.actor.core.extractor.WorkflowDtoExtractor.WorkflowExtracted
import ru.sber.cb.ap.gusli.actor.{BaseActor, Response}

object WorkflowComparer {
  def apply(current: ActorRef, prev: ActorRef, receiver: ActorRef): Props = Props(new WorkflowComparer(current, prev, receiver))

  case class WorkflowEquals(current: ActorRef, prev: ActorRef) extends Response

  case class WorkflowNotEquals(current: ActorRef, prev: ActorRef) extends Response

}

class WorkflowComparer(current: ActorRef, prev: ActorRef, receiver: ActorRef) extends BaseActor {

  import WorkflowComparer._

  var dto1: Option[WorkflowDto] = None
  var dto2: Option[WorkflowDto] = None

  var ext1: ActorRef = _
  var ext2: ActorRef = _

  override def preStart(): Unit = {
    ext1 = context.actorOf(WorkflowDtoExtractor(current, self))
    ext2 = context.actorOf(WorkflowDtoExtractor(prev, self))
  }


  override def receive: Receive = {
    case WorkflowExtracted(dto) =>
      if (sender() == ext1)
        dto1 = Some(dto)
      else if (sender() == ext2)
        dto2 = Some(dto)

      for (d1 <- dto1; d2 <- dto2) {
        if (d1 == d2)
          receiver ! WorkflowEquals(current, prev)
        else
          receiver ! WorkflowNotEquals(current, prev)

        context.stop(self)
      }
  }
}
