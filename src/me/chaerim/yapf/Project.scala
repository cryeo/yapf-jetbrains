package me.chaerim.yapf

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.{Project => IdeaProject}

object Project {
  implicit class RichProject(project: IdeaProject) {
    val settings: Settings = ServiceManager.getService(project, classOf[Settings])
  }
}
