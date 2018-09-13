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
  
  val cat = system.actorOf(Category(CategoryMetaDefault("category-empty")), "category-empty")
  
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
    
    "send ListSubcategory" should {
      "Send back SubcategoryList with empty actors ref" in {
        cat ! ListSubcategory()
        expectMsg(SubcategoryList(Nil))
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
  }
}