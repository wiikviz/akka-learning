package ru.sber.cb.ap.gusli.actor.projects.read

import java.nio.file.Path

import akka.actor.{ActorRef, PoisonPill, Props}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityRoot, GetEntityRoot}
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, Project, ProjectMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityFolderReader.ReadEntity
import ru.sber.cb.ap.gusli.actor.projects._
import ru.sber.cb.ap.gusli.actor.projects.read.entity.{EntityFolderReader, EntityFolderReaderMetaDefault}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object DirectoryProjectReader {
  def apply(path: Path): Props = Props(new DirectoryProjectReader(DirectoryProjectReaderMetaDefault(path)))
  
  case class ReadProject(replyTo: Option[ActorRef] = None) extends Request
  
  case class ProjectReaded(actorRef: ActorRef) extends Response
  case class ProjectNotReaded(path: Path) extends Response
}

case class DirectoryProjectReader(meta: DirectoryProjectReaderMeta) extends BaseActor {
  import DirectoryProjectReader._
  val path: Path = this.meta.path
//  private var nextStage: ActorRef = _//context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault()))
  
  override def receive: Receive = {
    case ReadProject(sendTo: Option[ActorRef]) =>
      val categoryMeta = initializeCategoryMeta()
      val project = createProject(categoryMeta)
      fillProjectWithEntities(project)
      sendTo.getOrElse(sender) ! ProjectReaded(project)
    
    case EntityRoot(entity) =>
      val entityReader = context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault(path.resolve("entity"), entity)))
      entityReader ! ReadEntity()
      Thread.sleep(1000)
      entityReader ! PoisonPill
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

trait DirectoryProjectReaderMeta {
  val path: Path
}

case class DirectoryProjectReaderMetaDefault(path: Path) extends DirectoryProjectReaderMeta