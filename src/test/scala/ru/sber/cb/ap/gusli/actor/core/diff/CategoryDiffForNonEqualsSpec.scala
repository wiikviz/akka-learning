package ru.sber.cb.ap.gusli.actor.core.diff

import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core._

class CategoryDiffForNonEqualsSpec extends ActorBaseTest("CategoryDiffForNonEqualsSpec") {

  import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer._

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val meta1 = CategoryMetaDefault("category", Map("p1"->"111", "p2"->"222"))
  private val meta2 = CategoryMetaDefault("category", Map("p2"->"111", "p1"->"222"))
  private val currentCat = system.actorOf(Category(meta1, projectProbe.ref))
  private val prevCat = system.actorOf(Category(meta2, projectProbe.ref))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "CategoryDiff for Category with the differ meta params" must {
    "return CategoryDelta" in {
      val projectDiffProbe = TestProbe()
      system.actorOf(CategoryDiffer(projectDiffProbe.ref, currentCat, prevCat, receiverProbe.ref))
      receiverProbe.expectMsgAnyClassOf(classOf[CategoryDelta])
    }
  }

}


