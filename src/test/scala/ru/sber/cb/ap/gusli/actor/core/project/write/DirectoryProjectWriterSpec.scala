package ru.sber.cb.ap.gusli.actor.core.project.write

import java.nio.file.Paths

import akka.actor.ActorRef
import akka.testkit.TestActor.Ignore
import ru.sber.cb.ap.gusli.actor.core.ActorBaseTest
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader.{ProjectReaded, ReadProject}
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter
import ru.sber.cb.ap.gusli.actor.projects.write.ProjectWriter.{ProjectWrited, WriteProject}


class DirectoryProjectWriterSpec extends ActorBaseTest("DirectoryProjectSpec") {
  val correctPath = Paths.get("./src/test/resources/project_test-2")
  val writePath = Paths.get("./src/test/resources/write")
  val directoryProjectReader: ActorRef = system.actorOf(DirectoryProjectReader(correctPath))
  
  "Directory project writer" when {
    var project: ActorRef = null
  
    "projectReader reads project" should {
      "wait until project haven't been readed" in {
        directoryProjectReader ! ReadProject()
        expectMsgPF() {
          case ProjectReaded(inputProject) => project = inputProject
        }
      }
    }
  
    "receive WriteProject" should {
      "send back ProjectWrited()" in {
        val directoryProjectWriter: ActorRef = system.actorOf(ProjectWriter(project, writePath))
        directoryProjectWriter ! WriteProject()
        expectMsg(ProjectWrited())
      }
    }
  }
}