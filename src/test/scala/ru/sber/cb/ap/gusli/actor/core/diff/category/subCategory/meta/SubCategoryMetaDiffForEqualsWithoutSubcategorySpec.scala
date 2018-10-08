package ru.sber.cb.ap.gusli.actor.core.diff.category.subCategory.meta

import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryMetaDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryMetaDiffer.SubCategoryMetaEquals

class SubCategoryMetaDiffForEqualsWithoutSubcategorySpec extends ActorBaseTest("SubCategoryMetaDiffForEqualsWithoutSubcategorySpec") {

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val c1Meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))

  private val c1 = system.actorOf(Category(c1Meta, projectProbe.ref), "c1")
  private val c1Copy = system.actorOf(Category(c1Meta, projectProbe.ref), "c1Copy")


  "A `SubCategoryMetaDiff` for Category with children with same meta" must {
    "return SubCategoryMetaEquals(c1,c1Copy)" in {
      system.actorOf(SubCategoryMetaDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(SubCategoryMetaEquals(c1, c1Copy))
    }
  }

}


