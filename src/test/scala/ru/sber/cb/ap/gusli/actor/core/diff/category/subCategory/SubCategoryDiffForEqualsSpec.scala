package ru.sber.cb.ap.gusli.actor.core.diff.category.subCategory

import akka.actor.ActorRef
import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.SubCategoryDiffer.SubCategoryEquals

class SubCategoryDiffForEqualsSpec extends ActorBaseTest("SubCategoryDiffForEqualsSpec") {

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val c1Meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))
  private val c1SubMeta = CategoryMetaDefault("c2", Map("p2" -> "222", "p1" -> "111"))

  private val c1 = system.actorOf(Category(c1Meta, projectProbe.ref), "c1")
  private val c1Copy = system.actorOf(Category(c1Meta, projectProbe.ref), "c1Copy")
  private var c1SubCat: ActorRef = _
  private var c1CopySubCat: ActorRef = _

  c1 ! AddSubcategory(c1SubMeta)
  expectMsgPF() {
    case SubcategoryCreated(sub) => c1SubCat = sub
  }
  c1Copy ! AddSubcategory(c1SubMeta)
  expectMsgPF() {
    case SubcategoryCreated(sub) => c1CopySubCat = sub
  }

  "A `SubCategoryDiff` for Category with children with same meta" must {
    "return SubCategoryEquals(c1,c1Copy)" in {
      system.actorOf(SubCategoryDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(SubCategoryEquals(c1, c1Copy))
    }
  }

}


