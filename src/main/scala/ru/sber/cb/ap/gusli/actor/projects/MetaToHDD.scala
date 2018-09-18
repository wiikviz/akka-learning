package ru.sber.cb.ap.gusli.actor.projects

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, ProjectMeta, WorkflowMeta}
import ru.sber.cb.ap.gusli.actor.projects.MetaToYamlSerialization._

object MetaToHDD {
  val charSet = StandardCharsets.UTF_8

  def writeProjectMetaToPath(meta: ProjectMeta, path: Path):Path =
    createNewFolder(meta.name, path)


  def writeCategoryMetaToPath(meta:CategoryMeta, dir:Path, parentMeta:CategoryMeta): Path ={
    val categoryFolder =  createNewFolder(meta.name, dir)




    categoryFolder
  }



  def writeWorkflowMetaToPath(meta:WorkflowMeta, dir:Path, categoryMeta: CategoryMeta): Path ={
    val workflowFolder = createNewFolder(meta.name, dir)



    workflowFolder
  }






  def writeEntityMetaToPath(meta: EntityMeta, dir:Path, parentMeta:EntityMeta, hasChildren:Boolean): Path ={
    val fileContent = convertEntityMetaToYAMLFileContent(meta, parentMeta)
    if(hasChildren){
      val entityFolder = createNewFolder(meta.name, dir)
      writeYAMLTextFileToDirectory(fileContent,"entity", entityFolder)
      entityFolder
    }
    else
      writeYAMLTextFileToDirectory(fileContent,s"${meta.id} ${meta.id}",dir)

  }

  private def createNewFolder(newFolderName:String, dirWereWillBeCreatedNewFolder:Path): Path = {
    Files.createDirectories(dirWereWillBeCreatedNewFolder resolve normalizeName(newFolderName))
  }

  private def normalizeName(folderName: String):String = {
    val tt = folderName.replace("/","~").replace(":","%3A").trim
    URLEncoder.encode(folderName, charSet)
  }
  def writeYAMLTextFileToDirectory(fileContent:String, fileName:String, dir:Path):Path ={
    Files.write(dir.resolve(fileName+".yaml"),fileContent.getBytes(charSet))
  }
}
