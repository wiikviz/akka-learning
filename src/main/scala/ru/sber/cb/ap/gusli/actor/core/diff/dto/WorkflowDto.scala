package ru.sber.cb.ap.gusli.actor.core.diff.dto

import ru.sber.cb.ap.gusli.actor.core.WorkflowMeta

case class WorkflowDto(name: String,
                       sql: Map[String, String],
                       sqlMap: Map[String, String] = Map.empty,
                       init: Map[String, String] = Map.empty,
                       user: Option[String] = None,
                       queue: Option[String] = None,
                       grenkiVersion: Option[String] = None,
                       params: Map[String, String] = Map.empty,
                       stats: Set[Long] = Set.empty,
                       entities: Set[Long] = Set.empty) extends WorkflowMeta