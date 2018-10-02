package ru.sber.cb.ap.gusli.actor.projects.read.category.create

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.{CategoryFolderRead, ReadCategoryFolder}
import ru.sber.cb.ap.gusli.actor.projects.read.category.{CategoryFolderReader, CategoryFolderReaderMetaDefault, ProjectMetaMaker}
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.CategoryCreator.{CategoryRead, ReadFolder}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.{CategoryOptionalFields, YamlFileMapper}
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object CategoryCreator {
  def apply(meta: CategoryCreatorMeta): Props = Props(new CategoryCreator(meta))
  
  case class ReadFolder(replyTo: Option[ActorRef] = None) extends Request
  
  case class CategoryRead() extends Response
}

class CategoryCreator(meta: CategoryCreatorMeta) extends BaseActor {
  override def receive: Receive = {
    case ReadFolder(replyTo) => this.meta.parentCategory ! GetCategoryMeta()
    case CategoryMetaResponse(meta) => tryCreateCategory(meta)
    case SubcategoryCreated(childCategory) =>
      context.actorOf(CategoryFolderReader(CategoryFolderReaderMetaDefault(this.meta.path, childCategory))) ! ReadCategoryFolder()
    case CategoryFolderRead(replyTo) =>
      context.parent ! CategoryRead()
      context.stop(self)
  }
  
  private def tryCreateCategory(meta: CategoryMeta): Unit = {
    val catMetaTemp: Option[CategoryOptionalFields] = extractMetaFileFields(meta)
    this.meta.parentCategory ! AddSubcategory(inheritMeta(meta, catMetaTemp))
  }
  
  private def extractMetaFileFields(meta: CategoryMeta) = YamlFileMapper.readToCategoryOptionalFields(this.meta.path)
  
  private def inheritMeta(meta: CategoryMeta, catMetaTemp: Option[CategoryOptionalFields]): CategoryMeta = {
    if (catMetaTemp.nonEmpty)
      ProjectMetaMaker.categoryNonEmptyMeta(meta, catMetaTemp.get)
    else
      meta.asInstanceOf[CategoryMetaDefault].copy(name = this.meta.path.getFileName.toString)
  }
}

trait CategoryCreatorMeta {
  val path: Path
  val parentCategory: ActorRef
}

case class CategoryCreatorMetaDefault(path: Path, parentCategory: ActorRef) extends CategoryCreatorMeta
