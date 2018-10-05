package ru.sber.cb.ap.gusli.actor.projects

object MetaFieldsComparer {
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
  
  /**
    * Creates set with differences of 2 sets.
    * If value exists in child set only, it adds with +.
    * Else if in parent set only, it adds with -, that is used for deleting.
    * <br> <br>
    * You can find examples in MetaFieldsComparerSpec
    * @param parent parent set
    * @param child child set
    * @tparam T String or Int
    * @return set with diffs
    */
  def diffSet[T](parent: Option[Set[T]], child: Option[Set[T]]): Option[Set[T]] = Some(diffSet(parent.get, child.get))
  
  def diffSet[T](parent: Set[T], child: Set[T]): Set[T] = {
    val p: Set[T] = (parent diff child).map {
      case v: Int => -v
      case v: Long => -v
      case v: String => "-" + v
    }.asInstanceOf[Set[T]]
    val c: Set[T] = child diff parent
    p ++ c
  }
  
  /**
    * Creates map with differences of 2 maps.
    * If value exists in child map only, it adds to result map.
    * Else if in parent map only, it adds with value equaled to delete symbol from Config.
    * <br> <br>
    * You can find examples in MetaFieldsComparerSpec
    * @param parent parent map
    * @param child child map
    * @return map with diffs
    */
  def diffMap(parent: Option[Map[String, String]], child: Option[Map[String, String]]): Option[Map[String, String]] = Some(diffMap(parent.get, child.get))
  
  def diffMap(parent: Map[String, String], child: Map[String, String], deleteSymbol: String = DirectoryReadWriteConfig.deleteSymbol): Map[String, String] = {
    val p = (parent.toSet diff child.toSet).toMap.map(m => (m._1, deleteSymbol))
    val c = (child.toSet diff parent.toSet).toMap
    p ++ c
  }
  
  def diffMapKeyset(parent: Map[String, String], child: Map[String, String]): Set[String] = diffMap(parent, child).keySet
  
  def diffField[T](parent: Option[T], child: Option[T]): Option[T] =
    if (child == parent)
      None
    else
      child
}