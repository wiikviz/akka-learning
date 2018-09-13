package category

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated, _}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class CategorySpec() extends TestKit(ActorSystem("CategorySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  
  "An empty Category" when {
    val cat = system.actorOf(Category(CategoryMetaDefault("category-empty")), "category-empty")
    "Send GetCategoryMeta" should {
      cat ! GetCategoryMeta()
      "send back CategoryMetaResponse(\"category-empty\")" in {
        expectMsg(CategoryMetaResponse("category-empty"))
      }
    }
  
    "Send ListSubcategory" should {
      "send back SubcategoryList with empty actors ref" in {
        cat ! ListSubcategory()
        expectMsg(SubcategoryList(Nil))
      }
    }
    
    "Send ListWorkflow message" should {
      cat ! ListWorkflow()
      "send back empty WorkflowList" in {
        expectMsg(WorkflowList(Nil))
      }
    }
  
    "Send AddSubcategory message" should {
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
  
    "Send AddWorkflow message" should {
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
  
    "Again send ListSubcategory" should {
      "send back SubcategoryList with non-empty actors ref" in {
        cat ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) => assert(list.nonEmpty); assert(list.size == 1)
        }
      }
    }
  
    "Again send ListWorkflow message" should {
      cat ! ListWorkflow()
      "send back WorkflowList non-empty WorkflowList" in {
        expectMsgPF() {
          case WorkflowList(list) => assert(list.nonEmpty); assert(list.size == 1)
        }
      }
    }
  
    "Again send AddSubcategory message" should {
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
  
    "Again send AddWorkflow message" should {
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
  
    "Again send ListSubcategory with 2 SubCategories" should {
      "send back SubcategoryList with non-empty actors ref" in {
        cat ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) => assert(list.nonEmpty); assert(list.size == 2)
        }
      }
    }
  
    "Again send ListWorkflow message with 2 Workflows" should {
      cat ! ListWorkflow()
      "send back WorkflowList non-empty WorkflowList" in {
        expectMsgPF() {
          case WorkflowList(list) => assert(list.nonEmpty); assert(list.size == 2)
        }
      }
    }
  }
}