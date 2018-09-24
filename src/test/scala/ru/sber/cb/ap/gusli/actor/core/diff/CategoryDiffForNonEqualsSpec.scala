package ru.sber.cb.ap.gusli.actor.core.diff

import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core.Category.{CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.core._

class CategoryDiffForNonEqualsSpec extends ActorBaseTest("CategoryDiffForNonEqualsSpec") {

  import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer._

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val currMeta = CategoryMetaDefault("category", Map("p1"->"111", "p2"->"222"))
  private val prevMeta = CategoryMetaDefault("category", Map("p2"->"111", "p1"->"222"))
  private val currentCat = system.actorOf(Category(currMeta, projectProbe.ref))
  private val prevCat = system.actorOf(Category(prevMeta, projectProbe.ref))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "CategoryDiff for Category with the differ meta params" must {
    "return CategoryDelta with current meta" in {
      val projectDiffProbe = TestProbe()
      system.actorOf(CategoryDiffer(projectDiffProbe.ref, currentCat, prevCat, receiverProbe.ref))
      receiverProbe.expectMsgPF()({
        case CategoryDelta(delta)=>
          delta ! GetCategoryMeta()
          expectMsg(CategoryMetaResponse(currMeta))
      })

      expectNoMessage()
    }
  }

}


