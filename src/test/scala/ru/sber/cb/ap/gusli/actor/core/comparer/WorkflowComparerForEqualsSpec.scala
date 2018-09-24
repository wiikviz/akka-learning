package ru.sber.cb.ap.gusli.actor.core.comparer

import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.BindEntity
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.comparer.WorkflowComparer.WorkflowEquals

import scala.concurrent.duration._

class WorkflowComparerForEqualsSpec extends ActorBaseTest("WorkflowComparerForEqualsSpec") {
  private val projectProbe = TestProbe()
  private val meta = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)
  private val wf = system.actorOf(Workflow(meta, projectProbe.ref))
  private val wfCopy = system.actorOf(Workflow(meta, projectProbe.ref))
  private val entityMeta = EntityMetaDefault(1, "e1", "/e1", None)
  private val entity1 = TestProbe()
  private val entity2 = TestProbe()

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "WorkflowComparer for workflows with the same meta and without entities " must {
    "be equals" in {
      val receiver = TestProbe()
      system.actorOf(WorkflowComparer(wf, wfCopy, receiver.ref))
      receiver.expectMsg(WorkflowEquals(wf, wfCopy))
    }

    "after add entities with same id" in {
      wf ! BindEntity(1)
      projectProbe.expectMsg(FindEntity(1))

      projectProbe.reply(EntityFound(entityMeta, entity1.ref))
      wfCopy ! BindEntity(1)
      projectProbe.expectMsg(FindEntity(1))
      projectProbe.reply(EntityFound(entityMeta, entity2.ref))
    }

    "be equals too" in {
      val receiver = TestProbe()
      system.actorOf(WorkflowComparer(wf, wfCopy, receiver.ref))
      entity1.expectMsg(GetEntityMeta())
      entity1.reply(EntityMetaResponse(entityMeta))
      entity2.expectMsg(GetEntityMeta())
      entity2.reply(EntityMetaResponse(entityMeta))
      receiver.expectMsg(WorkflowEquals(wf, wfCopy))
    }
  }

}


