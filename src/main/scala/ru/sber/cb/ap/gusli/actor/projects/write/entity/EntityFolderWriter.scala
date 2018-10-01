package ru.sber.cb.ap.gusli.actor.projects.write.entity

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.EntityMeta
import ru.sber.cb.ap.gusli.actor.projects.write.MetaToHDD
import ru.sber.cb.ap.gusli.actor.projects.write.entity.EntityFolderWriter.{EntityWrote, WriteEntity}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object EntityFolderWriter {
  def apply(meta: EntityFolderWriterMeta): Props = Props(new EntityFolderWriter(meta))
  
  case class WriteEntity(replyTo: Option[ActorRef] = None) extends Request
  
  case class EntityWrote() extends Response
  
}

class EntityFolderWriter(meta: EntityFolderWriterMeta) extends BaseActor {
  var childrenCount: Int = 0
  var entityMeta: EntityMeta = _
  var sendTo: ActorRef = _
  
  override def receive: Receive = {
    case WriteEntity(sendTo) =>
      this.sendTo = sendTo.getOrElse(sender())
      this.meta.entity ! GetEntityMeta()
    
    case EntityMetaResponse(meta) =>
      entityMeta = meta
      this.meta.entity ! GetChildren()
      
    case ChildrenEntityList(actorList) =>
      childrenCount = actorList.size
      val pathToThisEntity = MetaToHDD.writeEntityMetaToPath(entityMeta, this.meta.path, childrenCount)
      actorList.foreach {e =>
        pathToThisEntity match {
          case Right(p) => context.actorOf(EntityFolderWriter(EntityFolderWriterMetaDefault(p, e))) ! WriteEntity()
        }
      }

    case EntityWrote() =>
  }
}

trait EntityFolderWriterMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityFolderWriterMetaDefault(path: Path, entity: ActorRef) extends EntityFolderWriterMeta