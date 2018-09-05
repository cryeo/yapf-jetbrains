package me.chaerim.yapf

import com.intellij.openapi.components._
import com.intellij.util.xmlb.XmlSerializerUtil

import scala.beans.BeanProperty

@State(name = "YapfSettings", storages = Array(new Storage(StoragePathMacros.WORKSPACE_FILE)))
class Settings extends PersistentStateComponent[Settings] {
  @BeanProperty
  var formatOnSave: Boolean = false

  @BeanProperty
  var executablePath: String = Settings.DefaultExecutablePath

  @BeanProperty
  var styleFileName: String = Settings.DefaultStyleFileName

  override def loadState(config: Settings): Unit = XmlSerializerUtil.copyBean(config, this)

  override def getState: Settings = this
}

object Settings {
  val PluginName: String            = "YAPF"
  val DefaultStyleFileName: String  = ".style.yapf"
  val DefaultExecutablePath: String = "/usr/local/bin/yapf"
}
