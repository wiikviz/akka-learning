package ru.sber.cb.ap.gusli.actor.core.project.read

import org.scalatest.FlatSpec
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.MetaFieldsComparer._

class MetaFieldsComparerSpec extends FlatSpec {
  "MetaToChildInheritor.inheritSetOfLong" should "have distinct values when add equals longs" in {
    val set = inheritSetOfLong(Set[Long](1, 2), Some(Set[Long](2, 3)))
    assert(set.contains(1))
    assert(set.contains(2))
    assert(set.contains(3))
  }
  
  it should "delete values when add negative val" in {
    val set = inheritSetOfLong(Set[Long](1, 2), Some(Set[Long](-1, 3)))
    assert(!set.contains(1))
    assert(set.contains(2))
    assert(set.contains(3))
  }
  
  it should "doesn't break when delete unexistance id" in {
    val set = inheritSetOfLong(Set[Long](1, 2), Some(Set[Long](3, -4)))
    assert(set.contains(1))
    assert(set.contains(2))
    assert(set.contains(3))
    assert(!set.contains(4))
  }
  
  "MetaToChildInheritor.inheritMap" should "delete elems with delete symbol" in {
    val deleteSymbol = DirectoryReadWriteConfig.deleteSymbol
    val map = inheritMap(
      Map("i.s" -> "select 1", "i2.s" -> "select 1"),
      Option(Map("i.s" -> deleteSymbol, "i3.s" -> "select 1")))
    assert(!map.contains("i.s"))
    assert(map.contains("i2.s"))
    assert(map.contains("i3.s"))
  }
  
  it should "rewrite values from child" in {
    val deleteSymbol = DirectoryReadWriteConfig.deleteSymbol
    val map = inheritMap(
      Map("i.s" -> "select 1", "i2.s" -> "select 1"),
      Option(Map("i.s" -> "select 2", "i3.s" -> "select 1")))
    assert(map.get("i.s").contains("select 2"))
  }
  
  "MetaToChildInheritor.diffSet" should "create set[Int] with diffs" in {
    val p = Set(1, 2, 3, 4)
    val c = Set(3, 4, 5, 6)
    val res = Set(-1, -2, 5, 6)
    assert(res == diffSet(p, c))
  }
  
  it should "create set[String] with diffs" in {
    val del = DirectoryReadWriteConfig.deleteSymbol
    val p = Set("a", "b", "c", "d")
    val c = Set("c", "d", "e", "f")
    val res = Set(s"${del}a", s"${del}b", "e", "f")
    assert(res == diffSet(p, c))
  }

  it should "create set with new elems" in {
    val p: Set[Int] = Set.empty
    val c = Set(1, 2, 3, 4)
    val res = Set(4, 2, 3, 1)
    assert(res == diffSet(p, c))
  }
  
  it should "create empty set for equals sets" in {
    val s = Set(1, 2, 3, 4)
    val res = Set.empty
    assert(res == diffSet(s, s))
  }
  
  "MetaToChildInheritor.diffMap" should "create map with diffs" in {
    val del = DirectoryReadWriteConfig.deleteSymbol
    val parent = Map("0" -> "0", "1" -> "1", "2" -> "2")
    val child = Map("1" -> "1", "2" -> "new 2", "3" -> "3")
    val res = Map("0" -> del, "2" -> "new 2", "3" -> "3")
    assert(res == diffMap(parent, child))
  }
  
  it should "create empty map for equals maps" in {
    val m = Map("0" -> "0", "1" -> "1", "2" -> "2")
    val res = Map.empty
    assert(res == diffMap(m, m))
  }
}
