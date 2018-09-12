package category

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated, _}
import ru.sber.cb.ap.gusli.actor.core._

class CategorySpec() extends TestKit(ActorSystem("SubcategorySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  val cat = system.actorOf(Category(CategoryMetaDefault("category")), "category")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An empty Category" when {
    "Send ListSubcategory" should {
      "send back SubcategoryList with empty actor ref" in {
        cat ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryCreated(catA) =>
            catA ! GetCategoryMeta()
            expectMsg(CategoryMetaResponse("cat-a"))
        }
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
  }
}
