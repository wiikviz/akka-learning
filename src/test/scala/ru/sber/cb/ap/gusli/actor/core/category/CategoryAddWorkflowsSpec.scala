package ru.sber.cb.ap.gusli.actor.core.category

import akka.testkit.{ImplicitSender, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class CategoryAddWorkflowsSpec extends ActorBaseTest("CategoryAddWorkflowsSpec")
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  private val projectProbe: TestProbe = TestProbe()
  private val cat = system.actorOf(Category(CategoryMetaDefault("category", Map.empty), projectProbe.ref), "category")
  private val wf1 = TestProbe()
  private val wf2 = TestProbe()

  "An empty Category" when {
    "add AddWorkflows(Set(wf1,wf2))" should {
      cat ! AddWorkflows(Set(wf1.ref, wf2.ref))
      wf1.expectMsg(GetWorkflowMeta())
      wf1.reply(WorkflowMetaResponse(WorkflowMetaDefault("wf-1", Map.empty)))
      wf2.expectMsg(GetWorkflowMeta())
      wf2.reply(WorkflowMetaResponse(WorkflowMetaDefault("wf-2", Map.empty)))
      expectNoMessage()
      "Category contains added workflows" in {
        cat ! GetWorkflows()
        expectMsg(WorkflowSet(Set(wf1.ref, wf2.ref)))
      }
    }
  }
}