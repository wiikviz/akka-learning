package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import ru.sber.cb.ap.gusli.actor._
import ru.sber.cb.ap.gusli.actor.core.Entity.GetAllChildren
import ru.sber.cb.ap.gusli.actor.core.search.EntityCollector.{AllSubEntities, GetAllSubEntities}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class EntityCollector(val entitiList: Seq[ActorRef]) extends BaseActor{
  implicit val timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case GetAllSubEntities(sendTo) =>
      val tt: Seq[Future[Seq[ActorRef]]] = entitiList.map(_ ? GetAllChildren(sendTo))
      .map(_ map{case AllSubEntities(actorList) => actorList})
      val ttt: Seq[ActorRef] = tt.flatMap(Await.result(_, 1 second))
      sendTo getOrElse sender ! AllSubEntities(ttt ++ entitiList)
  }
}

object EntityCollector {
  def apply(entitiList: Seq[ActorRef])= Props(classOf[EntityCollector], entitiList)
  case class GetAllSubEntities(replyTo: Option[ActorRef] = None) extends Request
  case class AllSubEntities(actorList: Seq[ActorRef]) extends ActorListResponse
}
