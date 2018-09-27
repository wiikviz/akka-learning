package ru.sber.cb.ap.gusli.actor.projects.read

object MetaToChildInheritor {
  /**
    * Compare two sets. If parent
    * @param parentLongVals
    * @param childLongVals
    * @return
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
  
  def inheritMap(parentMap: Map[String, String], childMap: Option[Map[String, String]]): Map[String, String] = {
    val deleteSymbol = "-"
    if (childMap.isEmpty)
      parentMap
    else {
      val m: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map.empty
      m ++= parentMap
      childMap.getOrElse(Map.empty).foreach { case (k, v) =>
        if (v.startsWith(deleteSymbol))
          m.remove(k)
        else
          m += (k -> v)
      }
      m.toMap
    }
  }
}
