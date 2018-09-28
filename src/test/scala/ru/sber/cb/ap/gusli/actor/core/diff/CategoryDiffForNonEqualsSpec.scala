package ru.sber.cb.ap.gusli.actor.core.diff

import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Category.{CreateWorkflow, CategoryMetaResponse, GetCategoryMeta}
import ru.sber.cb.ap.gusli.actor.core._

class CategoryDiffForNonEqualsSpec extends ActorBaseTest("CategoryDiffForNonEqualsSpec") {

  import ru.sber.cb.ap.gusli.actor.core.diff.CategoryDiffer._

  private val receiverProbe = TestProbe()
  private val projectProbe = TestProbe()
  private val projectDiffProbe = TestProbe()
  private val currMeta = CategoryMetaDefault("category", Map("p1" -> "111", "p2" -> "222"))
  private val currentCat = system.actorOf(Category(currMeta, projectProbe.ref))
  //private val prevMeta = CategoryMetaDefault("category", Map("p2" -> "111", "p1" -> "222"))
  //private val prevCat = system.actorOf(Category(prevMeta, projectProbe.ref))

  "A CategoryDiffer for category with differ meta must return CategoryDelta" in {
    val prevMeta = CategoryMetaDefault("category", Map("p2" -> "111", "p1" -> "222"))
    val prevCat = system.actorOf(Category(prevMeta, projectProbe.ref))

    system.actorOf(CategoryDiffer(projectDiffProbe.ref, currentCat, prevCat, receiverProbe.ref))
    receiverProbe.expectMsgPF() {
      case CategoryDelta(delta) =>
        delta ! GetCategoryMeta()
        expectMsg(CategoryMetaResponse(currMeta))
    }

    expectNoMessage()
  }


//  "A CategoryDiffer for category with same meta but contains workflows with differ meta must return CategoryDelta" in {
//    val prevCat = system.actorOf(Category(currMeta, projectProbe.ref))
//    currentCat ! CreateWorkflow(WorkflowMetaDefault("test", Map("new.sql" -> "select 1")))
//    prevCat ! CreateWorkflow(WorkflowMetaDefault("test", Map("old.sql" -> "select 1")))
//
//    val projectDiffProbe = TestProbe()
//    system.actorOf(CategoryDiffer(projectDiffProbe.ref, currentCat, prevCat, receiverProbe.ref))
//    receiverProbe.expectMsgPF() {
//      case CategoryDelta(delta) =>
//        delta ! GetCategoryMeta()
//        expectMsg(CategoryMetaResponse(currMeta))
//    }
//
////    expectNoMessage()
//  }

}


