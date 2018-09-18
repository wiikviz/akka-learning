package ru.sber.cb.ap.gusli.actor.projects

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, WorkflowMeta}

object MetaToYamlSerialization {

  def convertCategoryMetaToYAMLFileContent(meta:CategoryMeta, parentMeta:CategoryMeta): String ={



    "CategoryMeta  YAMLfile  Content"
  }

  def convertWorkflowMetaToYAMLFileContent(meta:WorkflowMeta, categoryMeta: CategoryMeta): String ={



    "WorkflowMeta  YAMLfile  Content"
  }

  def convertEntityMetaToYAMLFileContent(meta: EntityMeta, parentMeta:EntityMeta): String ={



    "EntityMeta  YAMLfile  Content"
  }
}
