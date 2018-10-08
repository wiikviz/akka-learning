package ru.sber.cb.ap.gusli.actor.core.diff.category.subCategory

import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer.SubCategoryEquals

class SubCategoryDiffForEqualsWithoutSubcategoriesSpec extends ActorBaseTest("SubCategoryDiffForEqualsWithoutSubcategoriesSpec") {

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val c1Meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))

  private val c1 = system.actorOf(Category(c1Meta, projectProbe.ref), "c1")
  private val c1Copy = system.actorOf(Category(c1Meta, projectProbe.ref), "c1Copy")


  "A `SubCategoryDiff` for Category with children with same meta" must {
    "return SubCategoryEquals(c1,c1Copy)" in {
      system.actorOf(SubCategoryDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(SubCategoryEquals(c1, c1Copy))
    }
  }

}


