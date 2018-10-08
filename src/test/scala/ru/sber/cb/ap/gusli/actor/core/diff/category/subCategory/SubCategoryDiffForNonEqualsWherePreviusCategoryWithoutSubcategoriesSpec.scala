package ru.sber.cb.ap.gusli.actor.core.diff.category.subCategory

import akka.actor.ActorRef
import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer.SubCategoryDelta

class SubCategoryDiffForNonEqualsWherePreviousCategoryWithoutSubcategoriesSpec extends ActorBaseTest("SubCategoryDiffForNonEqualsWherePreviousCategoryWithoutSubcategoriesSpec") {
  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val c1Meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))
  private val c1SubMeta = CategoryMetaDefault("c2", Map("p2" -> "222", "p1" -> "111"))

  private val c1 = system.actorOf(Category(c1Meta, projectProbe.ref), "c1")
  private val c1Copy = system.actorOf(Category(c1Meta, projectProbe.ref), "c1Copy")
  private var c1SubCat: ActorRef = _

  c1 ! AddSubcategory(c1SubMeta)
  expectMsgPF() {
    case SubcategoryCreated(sub) => c1SubCat = sub
  }


  "A `SubCategoryDiff` for Category where only one of it contains subcategory" must {
    "return SubCategoryDelta(Set(c1SubCat))" in {
      system.actorOf(SubCategoryDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(SubCategoryDelta(Set(c1SubCat)))
    }
  }

}


