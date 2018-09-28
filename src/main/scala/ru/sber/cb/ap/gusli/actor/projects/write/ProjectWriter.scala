package ru.sber.cb.ap.gusli.actor.projects.write


import java.nio.file.{Files, Path}

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core.{CategoryMetaDefault, EntityMetaDefault}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

class ProjectWriter(val project: ActorRef, path: Path) extends BaseActor {
  
  import ProjectWriter._
  private var receiver: ActorRef = _
  
  override def receive: Receive = {
    
    case WriteProject(sendTo) =>
      receiver = sendTo.getOrElse(sender)
      project ! GetProjectMeta(Some(self))
    
    case ProjectMetaResponse(meta) =>
      val projectFolderPath = MetaToHDD.writeProjectMetaToPath(meta, path)
      val projectPath = path.resolve(meta.name)
      project ! GetCategoryRoot()
//      val categoryWriterActorRef = context actorOf CategoryWriter(projectFolderPath, CategoryMetaDefault("noName", Map("file" -> "noSQLfileContent")))
//      val entityWriterActorRef = context actorOf EntityWriter(projectFolderPath, EntityMetaDefault(id = -10, name = "noName", path = "noPath", parentId = None))
      
//      project ! GetCategoryRoot(Some(categoryWriterActorRef))
//      project ! GetEntityRoot(Some(entityWriterActorRef))

    case CategoryRoot(category) => checkFinish()
  
    case EntityRoot(entity) =>
      checkFinish()
  }
  
  private def checkFinish(): Unit = {
    receiver ! ProjectWrited()
  }
}

object ProjectWriter {
  def apply(project:ActorRef, path: Path): Props = Props(new ProjectWriter(project,path))

  case class WriteProject(replyTo: Option[ActorRef] = None) extends Request
  case class ProjectWrited() extends Response
  case class ProjectNotWrited() extends Response
}