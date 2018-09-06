package ru.sber.cb.ap.domain.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

import scala.collection.mutable.ListBuffer

class GroupingActor(count: Int, target: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("GroupingActor size={} target={}", count, target)

  val received = new ListBuffer[AnyRef]

  override def receive: Receive = {
    case msg: AnyRef =>
      received.append(msg)
      log.info("Receive {}/{} msg={}", received.size, count, msg)
      if (received.size == count) {
        target ! received.toList
        context.stop(self)
      }
    case x => throw new RuntimeException(s"WTF:$x")
  }
}

object GroupingActor {
  def apply(count: Int, target: ActorRef): Props = Props(new GroupingActor(count, target))
}