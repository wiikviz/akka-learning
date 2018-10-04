package ru.sber.cb.ap.gusli.actor.core.diff.category

import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer

class CategoryDiffWithChildrenForNonEqualsSpec extends ActorBaseTest("CategoryDiffWithChildrenForNonEqualsSpec") {

  import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer._

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val meta = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))
  private val subMeta = CategoryMetaDefault("c2", Map("p2" -> "222", "p1" -> "111"))

  private val c1 = system.actorOf(Category(meta, projectProbe.ref))
  private val c1Copy = system.actorOf(Category(meta, projectProbe.ref))

  c1 ! AddSubcategory(subMeta)
  expectMsgAnyClassOf(classOf[SubcategoryCreated])

  "A `CategoryDiff` for Category with the same meta but first category contains children" must {
    "return CategoryDelta(c1)" in {
      system.actorOf(CategoryDiffer(c1, c1Copy, receiverProbe.ref))
      receiverProbe.expectMsg(CategoryDelta(c1))
    }
  }

}


