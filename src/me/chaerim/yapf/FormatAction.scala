package me.chaerim.yapf

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.fileEditor.FileEditorManager

class FormatAction extends AnAction {
  override def actionPerformed(event: AnActionEvent): Unit =
    for {
      currentProject <- Option(event.getData(CommonDataKeys.PROJECT))
      currentEditor  <- Option(FileEditorManager.getInstance(currentProject).getSelectedTextEditor)
    } yield Document(currentEditor.getDocument).format
}
