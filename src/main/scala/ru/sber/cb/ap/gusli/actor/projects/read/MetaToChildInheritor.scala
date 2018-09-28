package ru.sber.cb.ap.gusli.actor.projects.read

import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig

object MetaToChildInheritor {
  /**
    * Compare two sets. Add positive child values to parent and remove abs(negative).<br>
    * <br>
    * <b>Example 1</b>: Set(1, 2), Set(2, 3) will return Set(1, 2, 3)<br>
    * <b>Example 2</b>: Set(1, 2), Set(-2, 3) will return Set(2, 3) without 1.<br>
    * @param parentLongVals
    * @param childLongVals
    * @return new set.
    */
  def inheritSetOfLong(parentLongVals: Set[Long], childLongVals: Option[Set[Long]]): Set[Long] = {
    if (childLongVals.isEmpty)
      parentLongVals
    else {
      val s: scala.collection.mutable.HashSet[Long] = scala.collection.mutable.HashSet.empty
      s ++= parentLongVals
      childLongVals.get.foreach(v => if (v >= 0) s += v else s -= v.abs)
      s.toSet
    }
  }
  
  /**
    * Compare two maps. Add or replace child values to parent, but remove if they have value marked as deleteSymbol.<br>
    * <br>
    * <b>Example 1</b>: Map("a" -> "a", "b" -> "b"), Map("b" -> "other b")<br>
    * will return Map("a" -> "a", "b" -> "other b")<br>
    * <b>Example 2</b>: Map("a" -> "a", "b" -> "b"), Map("b" -> "-")<br>
    * will return Map("a" -> "a")<br>
    * @param parentMap
    * @param childMap
    * @param deleteSymbol
    * @return new map.
    */
  def inheritMap(parentMap: Map[String, String], childMap: Option[Map[String, String]], deleteSymbol: String = DirectoryReadWriteConfig.deleteSymbol): Map[String, String] = {
    if (childMap.isEmpty)
      parentMap
    else {
      val m: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map.empty
      m ++= parentMap
      childMap.getOrElse(Map.empty).foreach { case (k, v) =>
        if (v == deleteSymbol)
          m.remove(k)
        else
          m += (k -> v)
      }
      m.toMap
    }
  }
}
