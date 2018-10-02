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
  
  case class Wrote() extends Response
  
}

class EntityRootWriter(meta: EntityRootWriterMeta) extends BaseActor {
  private var childrenCount: Int = 0
  private var entityMeta: EntityMeta = _
  private var sendTo: ActorRef = _
  private var answeredChildrenCount = 0
  
  override def receive: Receive = {
    
    case Write(sendTo) => getMeta(sendTo)
    
    case EntityMetaResponse(meta) =>
      entityMeta = meta
      getChildren()
    
    case ChildrenEntityList(list) => writeEntity(list)
    
    case EntityWrote() =>
      answeredChildrenCount += 1
      checkFinish()
  }
  
  private def writeEntity(list: Seq[ActorRef]) = {
    val pathToThisEntity = MetaToHDD.entityRoot(this.meta.path, entityMeta.name)
    childrenCount = list.size
    childrenCount match {
      case 0 => finish()
      case _ => writeChildren(list, pathToThisEntity)
    }
  }
  
  private def writeChildren(list: Seq[ActorRef],
    pathToThisEntity: Path): Unit = {
    list.foreach { e =>
      context.actorOf(EntityFolderWriter(EntityFolderWriterMetaDefault(pathToThisEntity, e))) ! WriteEntity()
    }
  }
  
  private def getMeta(sendTo: Option[ActorRef]) = {
    this.sendTo = sendTo.getOrElse(sender())
    this.meta.entity ! GetEntityMeta()
  }
  
  private def getChildren() = {
    this.meta.entity ! GetChildren()
  }
  
  private def checkFinish(): Unit = if (answeredChildrenCount == childrenCount) finish()
  
  private def finish(): Unit = {
    context.parent ! Wrote()
    context.stop(self)
  }
}

trait EntityRootWriterMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityRootWriterMetaDefault(path: Path, entity: ActorRef) extends EntityRootWriterMeta