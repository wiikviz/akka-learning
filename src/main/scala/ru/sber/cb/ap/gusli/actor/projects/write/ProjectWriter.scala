package ru.sber.cb.ap.gusli.actor.projects.write


import java.nio.file.{Files, Path}

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core.{CategoryMetaDefault, EntityMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.write.category.CategoryWriter
import ru.sber.cb.ap.gusli.actor.projects.write.entity.{EntityRootWriter, EntityRootWriterMetaDefault}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object ProjectWriter {
  def apply(project:ActorRef, path: Path): Props = Props(new ProjectWriter(project,path))
  
  case class WriteProject(replyTo: Option[ActorRef] = None) extends Request
  case class ProjectWrited() extends Response
  case class ProjectNotWrited() extends Response
}

class ProjectWriter(val project: ActorRef, path: Path) extends BaseActor {
  import ProjectWriter._
  private var projectFolderPath: Path = _
  private var receiver: ActorRef = _
  private var categoryRead = false
  private var entityRead = false
  
  override def receive: Receive = {
    
    case WriteProject(sendTo) =>
      receiver = sendTo.getOrElse(sender)
      project ! GetProjectMeta()
      
    case ProjectMetaResponse(meta) =>
      projectFolderPath = MetaToHDD.writeProjectMetaToPath(meta, path)
      writeEntities
      writeCategories
  
    case EntityRoot(entity) =>
      context.actorOf(EntityRootWriter(EntityRootWriterMetaDefault(projectFolderPath, entity))) ! EntityRootWriter.Write()
      
    case EntityRootWriter.Wrote() =>
      entityRead = true
      checkFinish()
  }
  
  private def writeEntities = project ! GetEntityRoot()
  
  private def writeCategories {
    val categoryWriter = context.actorOf(CategoryWriter(projectFolderPath, CategoryMetaDefault("temp-parent-for-root")))
    project ! GetCategoryRoot(Some(categoryWriter))
  }
  
  private def checkFinish(): Unit = if (entityRead & categoryRead) finish()
 
  private def finish(): Unit = receiver ! ProjectWrited()
}
