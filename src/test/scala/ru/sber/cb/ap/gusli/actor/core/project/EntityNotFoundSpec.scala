package ru.sber.cb.ap.gusli.actor.core.project

import akka.actor.ActorRef
import akka.testkit.TestKit
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._

class EntityNotFoundSpec extends ActorBaseTest("EntityNotFoundSpec") {

  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Project" when {
    "it's have no entities" should {
      "when receive FindEntity(1) send back EntityNotFound(1)" in {
        project ! FindEntity(1)
        expectMsg(EntityNotFound(1))
      }

      "and EntityNotFound(1) received only once time" in {
        expectNoMessage()
      }
    }
  }
}

