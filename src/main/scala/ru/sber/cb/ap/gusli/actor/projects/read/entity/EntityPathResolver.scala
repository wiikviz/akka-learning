package ru.sber.cb.ap.gusli.actor.projects.read.entity

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityFolderReader.ReadEntity
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityFolderResolver.ResolvePath

object EntityFolderResolver {
  def apply(meta: EntityFolderResolverMeta): Props = Props(new EntityFolderResolver(meta))
  
  case class ResolvePath(replyTo: Option[ActorRef] = None)
}

case class EntityFolderResolver(meta: EntityFolderResolverMeta) extends BaseActor {
  type Entity = ActorRef
  
  override def receive: Receive = {
    case ResolvePath(sendTo) =>
      this.meta.entity ! GetEntityMeta()
    case EntityMetaResponse(meta) =>
      val newPath = this.meta.path.resolve(s"${meta.id} ${meta.name}")
      val entityReader = context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault(newPath, this.meta.entity)))
      entityReader ! ReadEntity()
  }
  
}

trait EntityFolderResolverMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityFolderResolverMetaDefault(path: Path, entity: ActorRef) extends EntityFolderResolverMeta