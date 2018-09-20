package ru.sber.cb.ap.gusli.actor.projects.read.category

import java.nio.file.Path

import akka.actor.Props
import ru.sber.cb.ap.gusli.actor.BaseActor
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategory

object CategoryFolderReader {
  def apply(meta: CategoryFolderReaderMeta): Props = Props(new CategoryFolderReader(meta))
  
  case class ReadCategory()
  
}

class CategoryFolderReader(meta: CategoryFolderReaderMeta) extends BaseActor {
  override def receive: Receive = {
    case ReadCategory() =>
  }
}

trait CategoryFolderReaderMeta {
  val path: Path
}

case class CategoryFolderReaderMetaDefault(path: Path)