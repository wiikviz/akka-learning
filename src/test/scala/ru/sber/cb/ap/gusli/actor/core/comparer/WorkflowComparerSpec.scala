package ru.sber.cb.ap.gusli.actor.core.comparer

import akka.testkit.{TestKit, TestProbe}
import org.scalatest.Ignore
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.comparer.WorkflowComparer.WorkflowEquals

@Ignore
class WorkflowComparerSpec extends ActorBaseTest("WorkflowComparerSpec") {
  private val projectProbe = TestProbe()
  private val wf1Meta = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)
  private val wf11Meta = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)
  private val wf1 = system.actorOf(Workflow(wf1Meta, projectProbe.ref))
  private val wf11 = system.actorOf(Workflow(wf1Meta, projectProbe.ref))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "WorkflowComparer for workflows with the same meta and without entities " must {
    "be equals" in {
      val receiver = TestProbe()
      system.actorOf(WorkflowComparer(wf1, wf11, receiver.ref))
      receiver.expectMsg(WorkflowEquals(wf1, wf11))
    }
  }
}


