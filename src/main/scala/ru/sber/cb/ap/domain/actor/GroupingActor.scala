package ru.sber.cb.ap.domain.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

import scala.collection.mutable.ListBuffer

class GroupingActor(count: Int, target: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("GroupingActor size={} target={}", count, target)

  val received = new ListBuffer[AnyRef]

  override def receive: Receive = {
    case Terminated(targ) => context.stop(self)
    case msg: AnyRef =>
      log.info("Receive {}/{} msg={}", received.size, count, msg)
      received.append(msg)
      if (received.size == count) {
        target ! received.toList
        received.clear()
      }
  }
}

object GroupingActor {
  def apply(count: Int, target: ActorRef): Props = Props(new GroupingActor(count, target))
}