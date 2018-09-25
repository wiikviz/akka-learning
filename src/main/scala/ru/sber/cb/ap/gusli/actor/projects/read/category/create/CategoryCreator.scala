package ru.sber.cb.ap.gusli.actor.projects.read.category.create

import java.nio.file.Path

import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta, WorkflowCreated}
import ru.sber.cb.ap.gusli.actor.core.CategoryMeta
import ru.sber.cb.ap.gusli.actor.projects.read.category.create.CategoryCreator.ReadFolder
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request, Response}

object CategoryCreator {
  def apply(meta: CategoryCreatorMeta): Props = Props(new CategoryCreator(meta))
  
  case class ReadFolder(replyTo: Option[ActorRef] = None) extends Request
  
  case class FolderCreated(replyTo: Option[ActorRef] = None) extends Response
}

class CategoryCreator(meta: CategoryCreatorMeta) extends BaseActor {
  override def receive: Receive = {
    case ReadFolder(replyTo) => this.meta.category ! GetCategoryMeta()
    case CategoryMetaResponse(meta) => readCategoryFolder(meta)
    case WorkflowCreated(replyTo) =>
  }
  
  private def readCategoryFolder(meta: CategoryMeta) = {
    //compareCategories
  }
}

trait CategoryCreatorMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryCreatorMetaDefault(path: Path, category: ActorRef) extends CategoryCreatorMeta
