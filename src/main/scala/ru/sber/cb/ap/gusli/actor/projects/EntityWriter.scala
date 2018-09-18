package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.{Files, Path}

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.core.Category.GetCategoryMeta
import ru.sber.cb.ap.gusli.actor.core.{Entity, EntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.EntityRoot

class EntityWriter(path:Path, parentMeta:EntityMeta) extends BaseActor{

  var entityFolderPath:Path = _
  var meta:EntityMeta  = _
  override def receive: Receive = {

    //    case GetEntityRoot(sendTo) => sendTo getOrElse sender ! EntityRoot(entityRoot)  // EntityRoot(root: ActorRef)
    case EntityRoot(entityRootActorRef) =>
      entityRootActorRef ! GetEntityMeta(Some(context.self))
    case EntityMetaResponse(entityMeta) =>
      meta = entityMeta
      entityFolderPath = Files createDirectories path resolve entityMeta.name.replace("-","-")

      // todo write entityMeta to file
      sender ! Entity.GetChildren(Some(context.self))
    case ChildrenEntityList(actorList) =>
      for (subentity <- actorList){
        val entityWriterActorRef = context actorOf EntityWriter(entityFolderPath,meta)
        subentity ! GetCategoryMeta(Some(entityWriterActorRef))
      }
  }
}

object EntityWriter {



  def apply(path:Path,parentMeta:EntityMeta): Props = Props(new EntityWriter(path,parentMeta:EntityMeta))
}
