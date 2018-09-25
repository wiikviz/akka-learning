package ru.sber.cb.ap.gusli.actor.projects.read.category

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, WorkflowMeta, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.WorkflowOptionDto

object ParentCategoryMetaComparator {
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
      sqlMap = workflowMeta.sql.getOrElse(parentCategoryMeta.sqlMap),
      init = workflowMeta.init.getOrElse(parentCategoryMeta.init),
      user = workflowMeta.user.orElse(parentCategoryMeta.user),
      queue = workflowMeta.queue.orElse(parentCategoryMeta.queue),
      grenkiVersion = workflowMeta.grenkiVersion.orElse(parentCategoryMeta.grenkiVersion),
      params = workflowMeta.params.getOrElse(parentCategoryMeta.params),
      stats = workflowMeta.stats.getOrElse(parentCategoryMeta.stats)
    )
  }
}
