package me.chaerim.yapf

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class SettingsConfigurable(project: Project) extends SearchableConfigurable {
  private val settings: Settings   = Settings(project)
  private val panel: SettingsPanel = new SettingsPanel(settings)

  override def getDisplayName: String = Settings.PluginName

  override def getId: String = s"preference.${Settings.PluginName.toLowerCase}"

  override def getHelpTopic: String = s"reference.settings.${Settings.PluginName.toLowerCase}"

  override def enableSearch(option: String): Runnable = super.enableSearch(option)

  override def createComponent(): JComponent = panel.createPanel

  override def isModified: Boolean = panel.isModified

  override def disposeUIResources(): Unit = super.disposeUIResources()

  override def apply(): Unit = panel.apply

  override def reset(): Unit = panel.reset
}
