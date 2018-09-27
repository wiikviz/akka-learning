package ru.sber.cb.ap.gusli.actor.projects.read

import java.nio.file.Path

import akka.actor.{ActorRef, PoisonPill, Props}
import ru.sber.cb.ap.gusli.actor.core.Project.{CategoryRoot, EntityRoot, GetCategoryRoot, GetEntityRoot}
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, Project, ProjectMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.EntityFolderReader.{EntityRead, ReadEntity}
import ru.sber.cb.ap.gusli.actor.projects._
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.{CategoryFolderReader, CategoryFolderReaderMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.entity.{EntityFolderReader, EntityFolderReaderMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapper
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
  var project: ActorRef = _
  var answerReceiver: ActorRef = _
  
  override def receive: Receive = {
    case ReadProject(sendTo: Option[ActorRef]) =>
      answerReceiver = sendTo.getOrElse(sender)
      val categoryMeta = initializeCategoryMeta()
      project = createProject(categoryMeta)
      fillProjectWithEntities(project)
//      sendTo.getOrElse(sender) ! ProjectReaded(project)

    case EntityRoot(entity) =>
      val entityReader = context.actorOf(EntityFolderReader(EntityFolderReaderMetaDefault(path.resolve("entity"), entity)))
      entityReader ! ReadEntity()

    case EntityRead() => fillProjectWithCategories(project)
    
    case CategoryRoot(category) =>
      val categoryReader = context.actorOf(CategoryFolderReader(CategoryFolderReaderMetaDefault(path.resolve("category"), category)))
      categoryReader ! ReadCategoryFolder()
      answerReceiver ! ProjectReaded(project)
  }
  
  private def initializeCategoryMeta() =
    YamlFileMapper.readToCategoryMeta(path.resolve("category"))
    .getOrElse(CategoryMetaDefault("category"))
  
  private def createProject(categoryMeta: CategoryMeta): ActorRef = {
    val name = path.getFileName.toString
    context.actorOf(Project(ProjectMetaDefault(name), categoryMeta))
  }
  
  private def fillProjectWithEntities(project: ActorRef) = {
    project ! GetEntityRoot()
  }
  
  private def fillProjectWithCategories(project: ActorRef) = {
    project ! GetCategoryRoot()
  }
}

trait DirectoryProjectReaderMeta {
  val path: Path
}

case class DirectoryProjectReaderMetaDefault(path: Path) extends DirectoryProjectReaderMeta