package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Map;

public class SupplementNote extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        Editor editor = (Editor) event.getDataContext().getData("editor");
        if (editor == null) {
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null)
            return;
        StringAndOffset elements = InsertEditor.getAllElementAndOffset(element);

        Map<String, String> TextMap = null;
        String allText = elements.getString();
        TextMap = InsertEditor.stringToMap(allText);
        if (InitYML.noteMap.isEmpty()||InitYML.dataMap.isEmpty()) {
            InitYML initYML = new InitYML();
            initYML.actionPerformed(event);
        }
        if (allText != null && allText.contains("generate:")) {

            String printCode =InitYML.noteMap.get(TextMap.get("generate"));
            WriteCommandAction.runWriteCommandAction(project, () -> {
                editor.getDocument().insertString(elements.getOffset(),"\n"+printCode);
            });
        } else {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                editor.getDocument().insertString(elements.getOffset(), "please sure you select code contains \"gererate : keywords\"");
            });
        }


    }
}
