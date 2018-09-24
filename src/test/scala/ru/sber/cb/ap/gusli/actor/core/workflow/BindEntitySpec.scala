package ru.sber.cb.ap.gusli.actor.core.workflow

import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, EntityNotFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow._
import ru.sber.cb.ap.gusli.actor.core._


class BindEntitySpec extends ActorBaseTest("BindEntity") {
  val projectProbe = TestProbe()
  val workflow = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty), projectProbe.ref))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "bind entity to project where it's entity not exists" must {
    workflow ! BindEntity(1)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    projectProbe.reply(EntityNotFound(1))
    "return BindEntityFailedBecauseItNotExists(1)" in {
      expectMsg(BindEntityFailedBecauseItNotExists(1))
    }
  }

  "bind entity to project where it's entity exists" must {
    workflow ! BindEntity(2)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    val entity1 = TestProbe()
    projectProbe.reply(EntityFound(EntityMetaDefault(2, "entity2", "/path/2", None), entity1.ref))
    "return BindEntitySuccessful(2)" in {
      expectMsg(BindEntitySuccessful(2))
    }

    workflow ! BindEntity(3)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    val entity2 = TestProbe()
    projectProbe.reply(EntityFound(EntityMetaDefault(3, "entity3", "/path/3", None), entity2.ref))
    "return BindEntitySuccessful(3)" in {
      expectMsg(BindEntitySuccessful(3))
    }

    "return EntityList when receive ListEntities message" in {
      workflow ! GetEntityList()
      expectMsg(EntityList(List(entity1.ref, entity2.ref)))
    }
  }
}


