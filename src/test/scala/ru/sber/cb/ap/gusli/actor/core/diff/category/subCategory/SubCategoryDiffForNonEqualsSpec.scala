package ru.sber.cb.ap.gusli.actor.core.diff.category.subCategory

import akka.actor.ActorRef
import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer.SubCategoryDelta

class SubCategoryDiffForNonEqualsSpec extends ActorBaseTest("SubCategoryDiffForNonEqualsSpec") {

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val c1Meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))
  private val subMeta = CategoryMetaDefault("subMeta", Map("p2" -> "222", "p1" -> "111"))
  private val subMetaDiff = CategoryMetaDefault("subMetaDiff", Map("p2" -> "222", "p1" -> "111"))

  private val c1 = system.actorOf(Category(c1Meta, projectProbe.ref))
  private val c1Copy = system.actorOf(Category(c1Meta, projectProbe.ref))
  private var c1SubCat: ActorRef = _
  private var c1CopySubCat2: ActorRef = _

  c1 ! AddSubcategory(subMeta)
  expectMsgPF() {
    case SubcategoryCreated(sub) => c1SubCat = sub
  }
  c1Copy ! AddSubcategory(subMetaDiff)
  expectMsgPF() {
    case SubcategoryCreated(sub) => c1CopySubCat2 = sub
  }

  "A `SubCategoryDiff` for Category with different subcategories" must {
    "return SubCategoryDelta(Set(c1SubCat))" in {
      system.actorOf(SubCategoryDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(SubCategoryDelta(Set(c1SubCat)))
    }
  }

}


