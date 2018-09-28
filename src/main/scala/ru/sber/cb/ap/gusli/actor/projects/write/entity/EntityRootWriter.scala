package ru.sber.cb.ap.gusli.actor.projects.write.entity

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.EntityMeta
import ru.sber.cb.ap.gusli.actor.projects.write.entity.EntityRootWriter.{Write, Wrote}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object EntityRootWriter {
  def apply(meta: EntityRootWriterMeta): Props = Props(new EntityRootWriter(meta))
  
  case class Write(replyTo: Option[ActorRef] = None) extends Request
  case class Wrote(replyTo: Option[ActorRef] = None) extends Response
  
}

class EntityRootWriter(meta: EntityRootWriterMeta) extends BaseActor {
  val hasChildren: Boolean = false
  var entityMeta: EntityMeta = _
  
  override def receive: Receive = {
    case Write(sendTo) => this.meta.entity ! GetEntityMeta()
    case EntityMetaResponse(meta) =>
      entityMeta = meta
      this.meta.entity ! GetChildren()
    case ChildrenEntityList(list) =>
    
//    case EntityWrote(replyTo) =>
  }
}

trait EntityRootWriterMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityRootWriterMetaDefault(path: Path, entity: ActorRef) extends EntityRootWriterMeta