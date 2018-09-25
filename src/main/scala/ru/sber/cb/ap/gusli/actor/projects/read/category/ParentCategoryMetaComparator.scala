package ru.sber.cb.ap.gusli.actor.projects.read.category

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, WorkflowMeta, WorkflowMetaDefault}

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
}
