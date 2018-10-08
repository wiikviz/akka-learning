package ru.sber.cb.ap.gusli.actor.projects.read.category

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, WorkflowMeta, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.MetaFieldsComparer
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
      sqlMap = MetaFieldsComparer.inheritMap(parentCategoryMeta.sqlMap, workflowMeta.sqlMap),
      init = MetaFieldsComparer.inheritMap(parentCategoryMeta.init, workflowMeta.init),
      user = workflowMeta.user.orElse(parentCategoryMeta.user),
      queue = workflowMeta.queue.orElse(parentCategoryMeta.queue),
      grenkiVersion = workflowMeta.grenkiVersion.orElse(parentCategoryMeta.grenkiVersion),
      params = MetaFieldsComparer.inheritMap(parentCategoryMeta.params, workflowMeta.params),
      stats = MetaFieldsComparer. inheritSetOfLong(parentCategoryMeta.stats, workflowMeta.stats)
    )
  }
  
  def categoryNonEmptyMeta(parentCategoryMeta: CategoryMeta, childMeta: CategoryOptionalFields): CategoryMeta = {
    CategoryMetaDefault(
      name = childMeta.name,
      sqlMap = MetaFieldsComparer.inheritMap(parentCategoryMeta.sqlMap, childMeta.sqlMap),
      init = MetaFieldsComparer.inheritMap(parentCategoryMeta.init, childMeta.init),
      user = childMeta.user.orElse(parentCategoryMeta.user),
      queue = childMeta.queue.orElse(parentCategoryMeta.queue),
      grenkiVersion = childMeta.grenkiVersion.orElse(parentCategoryMeta.grenkiVersion),
      params = MetaFieldsComparer.inheritMap(parentCategoryMeta.params, childMeta.params),
      stats = MetaFieldsComparer.inheritSetOfLong(parentCategoryMeta.stats, childMeta.stats),
      entities = MetaFieldsComparer.inheritSetOfLong(parentCategoryMeta.entities, childMeta.entities)
    )
  }
}
