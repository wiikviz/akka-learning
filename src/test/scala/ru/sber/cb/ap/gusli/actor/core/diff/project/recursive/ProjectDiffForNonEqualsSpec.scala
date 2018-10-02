package ru.sber.cb.ap.gusli.actor.core.diff.project.recursive

import java.nio.file.Paths

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.core.ActorBaseTest
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader.{ProjectReaded, ReadProject}
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter.{ProjectWrited, WriteProject}

import scala.concurrent.duration._
import scala.concurrent.duration._

class ProjectDiffForNonEqualsSpec extends ActorBaseTest("ProjectDiffForNonEqualsSpec") {
  private val projectPath = Paths.get("./src/test/scala/ru/sber/cb/ap/gusli/actor/core/diff/project/recursive/data/project")
  private val projectCopyPath = Paths.get("./src/test/scala/ru/sber/cb/ap/gusli/actor/core/diff/project/recursive/data/project-copy")

  "Directory project differ" when {
    system.actorOf(DirectoryProjectReader(projectPath)) ! ReadProject()

    expectMsgPF() {
      case ProjectReaded(project) =>
        system.actorOf(DirectoryProjectReader(projectPath)) ! ReadProject()
        system.actorOf(DirectoryProjectReader(projectCopyPath)) ! ReadProject()
        expectMsgPF() {
          case ProjectReaded(projectCopy) =>
        }
    }
  }
}