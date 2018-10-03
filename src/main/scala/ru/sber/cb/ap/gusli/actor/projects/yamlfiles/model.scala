package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

//Single: "" -> Some(), empty -> None
//List: [] -> Some(Set()), empty -> None
//MapElem: "" -> "", empty-elem -> null
//Map: {} -> Some(Map()), empty -> None
case class CategoryFileFields(
  @JsonInclude(Include.NON_EMPTY) grenki: Option[String] = None,
  @JsonInclude(Include.NON_EMPTY) queue: Option[String] = None,
  @JsonInclude(Include.NON_EMPTY) user: Option[String] = None,
  init: Option[List[String]] = Some(List.empty),
  map: Option[List[String]] = Some(List.empty),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty)
) extends generalFileFields(grenki, queue, user, init, map, param, stats, entities)

case class WorkflowFileFields(
  @JsonInclude(Include.NON_EMPTY) grenki: Option[String] = None,
  @JsonInclude(Include.NON_EMPTY) queue: Option[String] = None,
  @JsonInclude(Include.NON_EMPTY) user: Option[String] = None,
  init: Option[List[String]] = Some(List.empty),
  map: Option[List[String]] = Some(List.empty),
  param: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Int]] = Some(Set.empty),
  entities: Option[Set[Int]] = Some(Set.empty),
  sql: Option[Set[String]]
) extends generalFileFields(grenki, queue, user, init, map, param, stats, entities) {
  
  override def toString(): String = super.toString() + "\nsql:" + sql
}

case class WorkflowOptionDto(
  name: Option[String] = None,
  grenkiVersion: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[Map[String, String]] = Some(Map.empty),
  sqlMap: Option[Map[String, String]] = Some(Map.empty),
  params: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Long]] = Some(Set.empty),
  entities: Option[Set[Long]] = Some(Set.empty),
  sql: Option[Map[String, String]]
)

case class CategoryOptionalFields(
  name: String,
  grenkiVersion: Option[String] = None,
  queue: Option[String] = None,
  user: Option[String] = None,
  init: Option[Map[String, String]] = Some(Map.empty),
  sqlMap: Option[Map[String, String]] = Some(Map.empty),
  params: Option[Map[String, String]] = Some(Map.empty),
  stats: Option[Set[Long]] = Some(Set.empty),
  entities: Option[Set[Long]] = Some(Set.empty)
)

abstract class generalFileFields(grenki: Option[String],
  queue: Option[String],
  user: Option[String],
  init: Option[List[String]],
  map: Option[List[String]],
  param: Option[Map[String, String]],
  stats: Option[Set[Int]],
  entities: Option[Set[Int]]) {
  
  override def toString() = {
    s"\ngrenki: " + grenki +
      s"\nqueue: " + queue +
      s"\nuser: " + user +
      s"\ninit: " + init +
      s"\nmap: " + map +
      s"\nparam: " + param +
      s"\nstats " + stats +
      s"\nentities: " + entities
  }
}