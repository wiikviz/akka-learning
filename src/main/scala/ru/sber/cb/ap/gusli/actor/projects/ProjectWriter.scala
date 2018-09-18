package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.{Files, Path}

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.{CategoryMetaDefault, EntityMetaDefault, ProjectMeta}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

class ProjectWriter(val project:ActorRef, path: Path) extends BaseActor {
  
  import ProjectWriter._
  
  override def receive: Receive = {
    case WriteProject(sendTo: Option[ActorRef]) =>
     sendTo getOrElse sender ! ProjectWrited()
      project ! GetProjectMeta(Some(context.self))
//    case GetProjectMeta(sendTo) => sendTo getOrElse sender ! ProjectMetaResponse(annotation.meta.name)
    case ProjectMetaResponse(meta:ProjectMeta) =>

      val projectFolderPath = Files createDirectories path resolve meta.name.replace("-","-")

      val categoryWriterActorRef = context actorOf CategoryWriter(projectFolderPath,CategoryMetaDefault("noName", Map("file" -> "noSQLfileContent")))
      val entityWriterActorRef = context actorOf EntityWriter(projectFolderPath,EntityMetaDefault(id= -10, name= "noName", path= "noPath", parentId = None))

      project ! GetCategoryRoot(Some(categoryWriterActorRef))
      project ! GetEntityRoot(Some(entityWriterActorRef))

  }
}


object ProjectWriter {
  def apply(project:ActorRef, path: Path): Props = Props(new ProjectWriter(project,path))

  case class WriteProject(replyTo: Option[ActorRef]) extends Request
  case class ProjectWrited() extends Response
  case class ProjectNotWrited() extends Response
}