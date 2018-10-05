package ru.sber.cb.ap.gusli.actor.projects.write

import ru.sber.cb.ap.gusli.actor.core.EntityMeta

object MetaToYamlSerialization {
  def convertEntityMetaToYAMLFileContent(meta: EntityMeta): String = s"path: ${meta.path}"
}
