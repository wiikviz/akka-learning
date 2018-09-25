package ru.sber.cb.ap.gusli.actor.projects.read.category

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, WorkflowMeta, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.{CategoryOptionalFields, WorkflowOptionDto}

object ProjectMetaMaker {
  def workflowEmptyMeta(parentCategoryMeta: CategoryMeta, workflowMeta: WorkflowMeta): WorkflowMeta = {
    WorkflowMetaDefault(
      name = workflowMeta.name,
      sql = workflowMeta.sql,
      sqlMap = parentCategoryMeta.sqlMap,
      init = parentCategoryMeta.init,
      user = parentCategoryMeta.user,
      queue = parentCategoryMeta.queue,
      grenkiVersion = parentCategoryMeta.grenkiVersion,
      params = parentCategoryMeta.params,
      stats = parentCategoryMeta.stats
    )
  }
  
  def workflowNonEmptyMeta(parentCategoryMeta: CategoryMeta, workflowMeta: WorkflowOptionDto): WorkflowMeta = {
    WorkflowMetaDefault(
      name = workflowMeta.name.get,
      sql = workflowMeta.sql.get,
      sqlMap = workflowMeta.sqlMap.getOrElse(parentCategoryMeta.sqlMap),
      init = workflowMeta.init.getOrElse(parentCategoryMeta.init),
      user = workflowMeta.user.orElse(parentCategoryMeta.user),
      queue = workflowMeta.queue.orElse(parentCategoryMeta.queue),
      grenkiVersion = workflowMeta.grenkiVersion.orElse(parentCategoryMeta.grenkiVersion),
      params = workflowMeta.params.getOrElse(parentCategoryMeta.params),
      stats = workflowMeta.stats.getOrElse(parentCategoryMeta.stats)
    )
  }
  
  def categoryNonEmptyMeta(parentCategoryMeta: CategoryMeta, childMeta: CategoryOptionalFields): CategoryMeta = {
    CategoryMetaDefault(
      name = childMeta.name,
      sqlMap = childMeta.sqlMap.getOrElse(parentCategoryMeta.sqlMap),
      init = childMeta.init.getOrElse(parentCategoryMeta.init),
      user = childMeta.user.orElse(parentCategoryMeta.user),
      queue = childMeta.queue.orElse(parentCategoryMeta.queue),
      grenkiVersion = childMeta.grenkiVersion.orElse(parentCategoryMeta.grenkiVersion),
      params = childMeta.params.getOrElse(parentCategoryMeta.params),
      stats = childMeta.stats.getOrElse(parentCategoryMeta.stats),
      entities = childMeta.entities.getOrElse(parentCategoryMeta.entities)
    )
  }
}
