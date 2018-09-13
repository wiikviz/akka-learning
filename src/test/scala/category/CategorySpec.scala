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

  val cat = system.actorOf(Category(CategoryMetaDefault("category")), "category")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An empty Category" when {
    "Send GetCategoryMeta" should {
      cat ! GetCategoryMeta()
      "send back CategoryMetaResponse(\"category\")" in {
        expectMsg(CategoryMetaResponse("category"))
      }
    }

    "Send ListSubcategory" should {
      "send back SubcategoryList with empty actors ref" in {
        cat ! ListSubcategory()
        expectMsg(SubcategoryList(Nil))
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

    "Send ListWorkflow message" should {
      cat ! ListWorkflow()
      "send back empty WorkflowList" in {
        expectMsgPF() {
          case WorkflowList(wfList) => 
        }
      }
    }
  }
}
