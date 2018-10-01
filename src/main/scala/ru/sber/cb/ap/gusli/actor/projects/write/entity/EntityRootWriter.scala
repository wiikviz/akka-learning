package ru.sber.cb.ap.gusli.actor.projects.write.entity

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.EntityMeta
import ru.sber.cb.ap.gusli.actor.projects.write.MetaToHDD
import ru.sber.cb.ap.gusli.actor.projects.write.entity.EntityFolderWriter.{EntityWrote, WriteEntity}
import ru.sber.cb.ap.gusli.actor.projects.write.entity.EntityRootWriter.{Write, Wrote}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object EntityRootWriter {
  def apply(meta: EntityRootWriterMeta): Props = Props(new EntityRootWriter(meta))
  
  case class Write(replyTo: Option[ActorRef] = None) extends Request
  case class Wrote(replyTo: Option[ActorRef] = None) extends Response
  
}

class EntityRootWriter(meta: EntityRootWriterMeta) extends BaseActor {
  var childrenCount: Int = 0
  var entityMeta: EntityMeta = _
  var sendTo: ActorRef = _
  
  override def receive: Receive = {
    case Write(sendTo) =>
      this.sendTo = sendTo.getOrElse(sender())
      this.meta.entity ! GetEntityMeta()
    
    case EntityMetaResponse(meta) =>
      entityMeta = meta
      this.meta.entity ! GetChildren()
    
    case ChildrenEntityList(list) =>
      val pathToThisEntity = MetaToHDD.entityRoot(this.meta.path, entityMeta.name)
      childrenCount = list.size
      list.foreach {e =>
        context.actorOf(EntityFolderWriter(EntityFolderWriterMetaDefault(pathToThisEntity, e))) ! WriteEntity()
      }
    
    case EntityWrote() =>
  }
}

trait EntityRootWriterMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityRootWriterMetaDefault(path: Path, entity: ActorRef) extends EntityRootWriterMeta