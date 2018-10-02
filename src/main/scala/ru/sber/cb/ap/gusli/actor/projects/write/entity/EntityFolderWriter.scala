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
  private var childrenCount: Int = 0
  private var entityMeta: EntityMeta = _
  private var sendTo: ActorRef = _
  private var answeredChildrenCount = 0
  
  override def receive: Receive = {
    case WriteEntity(sendTo) =>
      getMeta(sendTo)
    
    case EntityMetaResponse(meta) =>
      entityMeta = meta
      getChildren()
      
    case ChildrenEntityList(actorList) =>
      writeThisEntity(actorList)

    case EntityWrote() =>
      answeredChildrenCount += 1
      checkFinish()
  }
  
  private def writeThisEntity(actorList: Seq[ActorRef]): Unit = {
    childrenCount = actorList.size
    val pathToThisEntity: Either[Path, Path] = MetaToHDD.writeEntityMetaToPath(entityMeta, this.meta.path, childrenCount)
    childrenCount match {
      case 0 => finish()
      case _ => writeChildren(actorList, pathToThisEntity)
    }
  }
  
  private def writeChildren(actorList: Seq[ActorRef], pathToThisEntity: Either[Path, Path]): Unit = {
    actorList.foreach { e =>
      pathToThisEntity match {
        case Right(p) => context.actorOf(EntityFolderWriter(EntityFolderWriterMetaDefault(p, e))) ! WriteEntity()
      }
    }
  }
  
  private def getMeta(sendTo: Option[ActorRef]) = {
    this.sendTo = sendTo.getOrElse(sender())
    this.meta.entity ! GetEntityMeta()
  }
  
  private def getChildren() = this.meta.entity ! GetChildren()
  
  private def checkFinish(): Unit = if (answeredChildrenCount == childrenCount) finish()
  
  private def finish(): Unit = {
    context.parent ! EntityWrote()
    context.stop(self)
  }
}

trait EntityFolderWriterMeta {
  val path: Path
  val entity: ActorRef
}

case class EntityFolderWriterMetaDefault(path: Path, entity: ActorRef) extends EntityFolderWriterMeta