package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path
import akka.actor.{ActorRef, Props}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryPathResolver.ResolvePath
import ru.sber.cb.ap.gusli.actor.{BaseActor, Request}

object CategoryPathResolver {
  def apply(meta: CategoryPathResolverMeta): Props = Props(new CategoryPathResolver(meta))
  
  case class ResolvePath(replyTo: Option[ActorRef] = None) extends Request
}

class CategoryPathResolver(meta: CategoryPathResolverMeta) extends BaseActor {
  override def receive: Receive = {
    case ResolvePath(sendTo) => this.meta.category ! GetCategoryMeta()
    case CategoryMetaResponse(meta) =>
      val newPath = this.meta.path.resolve(s"${meta.name}")
      val categoryReader = context.actorOf(CategoryFolderReader(CategoryFolderReaderMetaDefault(newPath, this.meta.category)))
      categoryReader ! ReadCategoryFolder()
  }
}

trait CategoryPathResolverMeta {
  val path: Path
  val category: ActorRef
}

case class CategoryPathResolverMetaDefault(path: Path, category: ActorRef)