package ru.sber.cb.ap.gusli.actor.projects.write


import java.nio.file.{Files, Path}

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core.{CategoryMetaDefault, EntityMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.write.entity.{EntityRootWriter, EntityRootWriterMetaDefault}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

class ProjectWriter(val project: ActorRef, path: Path) extends BaseActor {
  import ProjectWriter._
  private var projectFolderPath: Path = _
  private var receiver: ActorRef = _
  private var categoryRead = false
  private var entityRead = false
  
  override def receive: Receive = {
    
    case WriteProject(sendTo) =>
      receiver = sendTo.getOrElse(sender)
      project ! GetProjectMeta(Some(self))
      
    case ProjectMetaResponse(meta) =>
      projectFolderPath = MetaToHDD.writeProjectMetaToPath(meta, path)
//      val projectPath = path.resolve(meta.name)
      project ! GetCategoryRoot()
      project ! GetEntityRoot()

    case CategoryRoot(category) =>
      categoryRead = true
      checkFinish()
  
    case EntityRoot(entity) =>
      context.actorOf(EntityRootWriter(EntityRootWriterMetaDefault(projectFolderPath, entity))) ! EntityRootWriter.Write()
      entityRead = true
      checkFinish()
  }
  
  private def checkFinish(): Unit = if (entityRead & categoryRead) receiver ! ProjectWrited()
}

object ProjectWriter {
  def apply(project:ActorRef, path: Path): Props = Props(new ProjectWriter(project,path))

  case class WriteProject(replyTo: Option[ActorRef] = None) extends Request
  case class ProjectWrited() extends Response
  case class ProjectNotWrited() extends Response
}