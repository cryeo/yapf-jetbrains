package me.chaerim.yapf

import java.awt._

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.{TextComponentAccessor, TextFieldWithBrowseButton}
import com.intellij.ui.IdeBorderFactory
import com.intellij.uiDesigner.core.{GridConstraints, GridLayoutManager, Spacer}
import javax.swing._

final class SettingsPanel(val settings: Settings) {
  import GridConstraints._

  private val panel: JPanel = new JPanel(gridLayoutManager(3, 1))

  private val yapfPanel: JPanel   = new JPanel(gridLayoutManager(2, 2))
  private val pluginPanel: JPanel = new JPanel(gridLayoutManager(1, 1))

  private val formatOnSaveCheckBox: JCheckBox                = new JCheckBox("Format on save")
  private val executablePathField: TextFieldWithBrowseButton = new TextFieldWithBrowseButton
  private val styleFileNameField: JTextField                 = new JTextField(Settings.DefaultStyleFileName)

  private val fileChooserDescriptor: FileChooserDescriptor =
    new FileChooserDescriptor(true, false, false, false, false, false)

  def createPanel: JComponent = {
    // NOTE[cryeo]: set plugin panel
    pluginPanel.setBorder(IdeBorderFactory.createTitledBorder("Plugin settings"))

    pluginPanel.add(
      formatOnSaveCheckBox,
      gridConstraints(0, 0, FILL_NONE, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED)
    )

    // NOTE[cryeo]: set yapf panel
    executablePathField.addBrowseFolderListener("YAPF executable path",
                                                "YAPF executable path",
                                                null,
                                                fileChooserDescriptor,
                                                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)

    yapfPanel.setBorder(IdeBorderFactory.createTitledBorder("YAPF settings"))

    yapfPanel.add(
      new JLabel("Executable path: "),
      gridConstraints(0, 0, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED)
    )

    yapfPanel.add(
      executablePathField,
      gridConstraints(0, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED)
    )

    yapfPanel.add(
      new JLabel("Style file name: "),
      gridConstraints(1, 0, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED)
    )

    yapfPanel.add(
      styleFileNameField,
      gridConstraints(1, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED)
    )

    // // NOTE[cryeo]: set entire panel
    panel.add(
      pluginPanel,
      gridConstraints(0, 0, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED)
    )

    panel.add(
      yapfPanel,
      gridConstraints(1, 0, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED)
    )

    panel.add(
      new Spacer,
      gridConstraints(2, 0, FILL_VERTICAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_WANT_GROW)
    )

    panel
  }

  def apply: Unit = {
    settings.formatOnSave   = formatOnSaveCheckBox.isSelected
    settings.executablePath = executablePathField.getText
    settings.styleFileName  = styleFileNameField.getText
  }

  def reset: Unit = {
    formatOnSaveCheckBox.setSelected(settings.formatOnSave)
    executablePathField.setText(settings.executablePath)
    styleFileNameField.setText(settings.styleFileName)
  }

  def isModified: Boolean =
    settings.formatOnSave != formatOnSaveCheckBox.isSelected ||
      settings.executablePath != executablePathField.getText ||
      settings.styleFileName == styleFileNameField.getText

  private def gridLayoutManager(row: Int, col: Int): GridLayoutManager =
    new GridLayoutManager(row, col, new Insets(0, 0, 0, 0), -1, -1)

  private def gridConstraints(row: Int, col: Int, fill: Int, hSizePolicy: Int, vSizePolicy: Int): GridConstraints =
    new GridConstraints(row,
                        col,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        fill,
                        hSizePolicy,
                        vSizePolicy,
                        null,
                        null,
                        null,
                        0,
                        false)
}
