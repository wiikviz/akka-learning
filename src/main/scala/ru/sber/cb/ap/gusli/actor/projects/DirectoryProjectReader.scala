package ru.sber.cb.ap.gusli.actor.projects

import java.nio.file.Path

import akka.actor.{ActorRef, PoisonPill, Props}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityRoot, GetEntityRoot}
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, Project, ProjectMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.EntityFolderReader.ReadEntity
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object DirectoryProjectReader {
  def apply(): Props = Props(new DirectoryProjectReader())
  
  case class ReadProject(path: Path, replyTo: Option[ActorRef] = None) extends Request
  
  case class ProjectReaded(actorRef: ActorRef) extends Response
  case class ProjectNotReaded(path: Path) extends Response
}

class DirectoryProjectReader() extends BaseActor {
  import DirectoryProjectReader._
  var path: Path = _
//  private var nextStage: ActorRef = _//context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault()))
  
  override def receive: Receive = {
    case ReadProject(pathToReadFolder: Path, sendTo: Option[ActorRef]) =>
      this.path = pathToReadFolder
      val categoryMeta = initializeCategoryMeta()
      val project = createProject(categoryMeta)
      fillProjectWithEntities(project)
      sendTo.getOrElse(sender) ! ProjectReaded(project)
    
    case EntityRoot(entity) =>
      val entityReader = context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault(path.resolve("entity"), entity)))
      entityReader ! ReadEntity()
      entityReader ! PoisonPill
      self ! PoisonPill
  }
  
  private def initializeCategoryMeta() = {
    CategoryMetaDefault("category")
  }
  
  private def createProject(categoryMeta: CategoryMeta) = {
    val name = path.getFileName.toString
    context.actorOf(Project(ProjectMetaDefault(name)))
  }
  
  private def fillProjectWithEntities(project: ActorRef) = {
    project ! GetEntityRoot()
  }
  
  private def fillProjectWithCategories(project: ActorRef) = {
  
  }
}