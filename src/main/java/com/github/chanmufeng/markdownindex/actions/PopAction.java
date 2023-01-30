package com.github.chanmufeng.markdownindex.actions;

import com.github.chanmufeng.markdownindex.services.MarkdownIndexService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;

/**
 * 弹出框
 */
public class PopAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFile file = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        Document document = editor.getDocument();
        String extension = file.getExtension();

        if (!"md".equalsIgnoreCase(extension)) {
            String title = String.format("文件类型错误");
            String message = "<br>该功能仅对markdown文件有效，请在正确的文件下执行此操作！<br>";
            Messages.showMessageDialog(project, message, title, Messages.getInformationIcon());
            return;
        }

        String[] lines = document.getText().split("\n");

        // 获取自己编写的MarkdownIndexService
        MarkdownIndexService markdownIndexService =
                ApplicationManager.getApplication().getService(MarkdownIndexService.class);
        lines = markdownIndexService.addMarkdownIndex(lines);

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        primaryCaret.removeSelection();

        //获取最后一行的结束边界
        int lineCount = document.getLineCount();
        int lineEndOffset = document.getLineEndOffset(lineCount - 1);

        final String indexContent = StringUtils.join(lines, "\n");

        //使用添加索引之后的内容覆盖掉原来的内容
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(0, lineEndOffset, indexContent);
        });


    }
}
