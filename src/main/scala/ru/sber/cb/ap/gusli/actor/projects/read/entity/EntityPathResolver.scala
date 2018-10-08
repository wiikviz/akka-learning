package ru.sber.cb.ap.gusli.actor.projects.read.entity

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityFolderReader.{EntityRead, ReadEntity}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityPathResolver.{PathResolved, ResolvePath}

object EntityPathResolver {
  def apply(meta: EntityPathResolverMeta): Props = Props(new EntityPathResolver(meta))
  
  case class ResolvePath(replyTo: Option[ActorRef] = None) extends Request
  
  case class PathResolved() extends Response
}

case class EntityPathResolver(meta: EntityPathResolverMeta) extends BaseActor {
  type Entity = ActorRef
  
  override def receive: Receive = {
    case ResolvePath(sendTo) => this.meta.entity ! GetEntityMeta()
    case EntityMetaResponse(meta) =>
      val newPath = this.meta.path.resolve(s"${meta.id} ${meta.name}")
      if (newPath.toFile.exists) {
        val entityReader = context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault(newPath, this.meta.entity)))
        entityReader ! ReadEntity()
      } else
        sendAnswerToParent()
    case EntityRead() => sendAnswerToParent()
  }
  
  private def sendAnswerToParent(): Unit =
    context.parent ! PathResolved()
}

trait EntityPathResolverMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityPathResolverMetaDefault(path: Path, entity: ActorRef) extends EntityPathResolverMeta