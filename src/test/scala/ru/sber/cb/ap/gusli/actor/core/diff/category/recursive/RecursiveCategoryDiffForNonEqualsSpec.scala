package ru.sber.cb.ap.gusli.actor.core.diff.category.recursive

import java.nio.file.Paths

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.core.ActorBaseTest
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader.{ProjectReaded, ReadProject}
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter.{ProjectWrited, WriteProject}

import scala.concurrent.duration._
import scala.concurrent.duration._

class RecursiveCategoryDiffForNonEqualsSpec extends ActorBaseTest("RecursiveCategoryDiffForNonEqualsSpec") {
  //private val correctPath = Paths.get("./src/test/resources/project_k")
  private val correctPath = Paths.get("./src/gucli/src/test/scala/ru/sber/cb/ap/gusli/actor/core/diff/category/recursive/data/project")
  private val directoryProjectReader: ActorRef = system.actorOf(DirectoryProjectReader(correctPath))

  directoryProjectReader ! ReadProject()

  "Directory project differ" when {
    expectMsgPF() {
      case ProjectReaded(project) =>
    }
  }
}