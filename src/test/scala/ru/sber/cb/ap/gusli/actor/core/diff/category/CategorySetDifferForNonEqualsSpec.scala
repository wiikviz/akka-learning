package ru.sber.cb.ap.gusli.actor.core.diff.category

import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{AddSubcategory, SubcategoryCreated}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.diff.CategorySetDiffer
import ru.sber.cb.ap.gusli.actor.core.diff.CategorySetDiffer.CategorySetDelta
import scala.concurrent.duration._

class CategorySetDifferForNonEqualsSpec extends ActorBaseTest("CategorySetDifferForNonEqualsSpec") {

  private val projectProbe = TestProbe()
  private val receiverProbe = TestProbe()
  private val m1 = CategoryMetaDefault("c1", Map("p1" -> "111", "p2" -> "222"))
  private val m11 = CategoryMetaDefault("c11", Map("c11" -> "true"))
  private val m2 = CategoryMetaDefault("c2", Map("a" -> "a", "b" -> "b"))
  private val m3 = CategoryMetaDefault("c3", Map("m" -> "d"))
  private val c1 = system.actorOf(Category(m1, projectProbe.ref), "c1")
  private val c1copy = system.actorOf(Category(m1, projectProbe.ref), "c1copy")
  private val c2 = system.actorOf(Category(m2, projectProbe.ref), "c2")
  private val c2copy = system.actorOf(Category(m2, projectProbe.ref), "c2copy")
  private val c3 = system.actorOf(Category(m3, projectProbe.ref), "c3")

  c1 ! AddSubcategory(m11)
  expectMsgAnyClassOf(classOf[SubcategoryCreated])

  "A `CategorySetDiffer` for differs sets" must {
    "return CategorySetDelta" in {
      system.actorOf(CategorySetDiffer(Set(c1, c2, c3), Set(c1copy, c2copy), receiverProbe.ref))
      receiverProbe.expectMsg(1 hour,CategorySetDelta(Set(c1, c3)))
    }
  }

}


