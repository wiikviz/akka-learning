package ru.sber.cb.ap.gusli.actor.core.category

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated, _}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class CategorySpec extends TestKit(ActorSystem("CategorySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  
  private val probe: TestProbe = TestProbe()
  val cat = system.actorOf(Category(CategoryMetaDefault("category"), probe.ref), "category")
  
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  
  "An empty Category" when {
    "send GetCategoryMeta" should {
      cat ! GetCategoryMeta()
      "send back CategoryMetaResponse(\"category\")" in {
        expectMsg(CategoryMetaResponse("category"))
      }
    }
    
    "send first time ListSubcategory" should {
      "send back SubcategoryList with empty actors ref" in {
        cat ! ListSubcategory()
        expectMsg(SubcategoryList(Nil))
      }
    }
  
    "send first time ListWorkflow" should {
      "send back WorkflowList with empty actors ref" in {
        cat ! ListWorkflow()
        expectMsg(WorkflowList(Nil))
      }
    }
    
    "send AddSubcategory" should {
      "send back SubcategoryCreated" in {
        val meta = CategoryMetaDefault("cat-a")
        cat ! AddSubcategory(meta)
        expectMsgPF() {
          case SubcategoryCreated(catA) =>
            catA ! GetCategoryMeta()
            expectMsg(CategoryMetaResponse("cat-a"))
        }
      }
    }
    
    "send AddWorkflow" should {
      "send back WorkflowCreated" in {
        val meta = WorkflowMetaDefault("wf-1", "file.sql")
        cat ! AddWorkflow(meta)
        expectMsgPF() {
          case WorkflowCreated(wf) =>
            wf ! GetWorkflowMeta()
            expectMsg(WorkflowMetaResponse("wf-1", "file.sql"))
        }
      }
    }
  
    "send second time ListSubcategory" should {
      "send back SubcategoryList with 1 actor ref" in {
        cat ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) => assert(list.size == 1)
        }
      }
    }
  
    "send second time ListWorkflow" should {
      "send back WorkflowList with 1 actor ref" in {
        cat ! ListWorkflow()
        expectMsgPF() {
          case WorkflowList(list) => assert(list.size == 1)
        }      }
    }
  
  
    "send second time AddSubcategory" should {
      "send back SubcategoryCreated" in {
        val meta = CategoryMetaDefault("cat-b")
        cat ! AddSubcategory(meta)
        expectMsgPF() {
          case SubcategoryCreated(catA) =>
            catA ! GetCategoryMeta()
            expectMsg(CategoryMetaResponse("cat-b"))
        }
      }
    }
  
    "send second time AddWorkflow" should {
      "send back WorkflowCreated" in {
        val meta = WorkflowMetaDefault("wf-2", "file.sql")
        cat ! AddWorkflow(meta)
        expectMsgPF() {
          case WorkflowCreated(wf) =>
            wf ! GetWorkflowMeta()
            expectMsg(WorkflowMetaResponse("wf-2", "file.sql"))
        }
      }
    }
  
    "send third time ListSubcategory" should {
      "send back SubcategoryList with empty actors ref" in {
        cat ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) => assert(list.size == 2)
        }
      }
    }
  
    "send third time ListWorkflow" should {
      "send back WorkflowList with empty actors ref" in {
        cat ! ListWorkflow()
        expectMsgPF() {
          case WorkflowList(list) => assert(list.size == 2)
        }
      }
    }
  }
}