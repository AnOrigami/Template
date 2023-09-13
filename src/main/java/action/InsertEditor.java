package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.HashMap;
import java.util.Map;

public class InsertEditor extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
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
        StringAndOffset elements = getAllElementAndOffset(element);



        Map<String, String> TextMap = null;
        String allText = elements.getString();
        TextMap = stringToMap(allText);
        if (InitYML.noteMap.isEmpty()||InitYML.dataMap.isEmpty()) {
            InitYML initYML = new InitYML();
            initYML.actionPerformed(event);
        }
        if (allText != null && allText.contains("generate:")) {


            String printCode = formatPrintCode(InitYML.dataMap, TextMap);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                editor.getDocument().insertString(elements.getOffset(),"\n"+printCode);
            });
        } else {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                editor.getDocument().insertString(elements.getOffset(), "please sure you select code contains \"gererate:\"");
            });
        }
    }

    public static StringAndOffset getAllElementAndOffset(PsiElement element) {
        StringAndOffset elements = new StringAndOffset();
        elements.setString("");
        PsiElement e = element;
        boolean b;
        while (true) {
            b = _continue(e.getText());
            if (!b) break;
            elements.setString(elements.getString() + formateString(e.getText()));
            e = e.getNextSibling();
        }
        elements.setOffset(e.getTextOffset());
        e = element.getPrevSibling();
        while (true) {
            b = _continue(e.getText());
            if (!b) break;
            elements.setString(formateString(e.getText()) + elements.getString());
            e = e.getPrevSibling();
        }
        return elements;

    }


    private static boolean _continue(String e) {
        int count = 0;
        int index = 0;
        while (index != -1) {
            index = e.indexOf("\n", index);
            if (index != -1) {
                count++;
                index += "\n".length();
            }
        }
        if (count >= 2) return false;
        else return true;

    }

    public static String formateString(String s) {
        String code = s.replaceAll("/{2,}", "/").replaceAll("[\n\r]", "").replaceAll("\\s+", "");
        return code;
    }

    public static String formatPrintCode(Map<String, String> ymlMap, Map<String, String> selectedMap) {
        String printCode = ymlMap.get(selectedMap.get("generate"));
        String formateCode = "";
        for (Map.Entry<String, String> entry : selectedMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!key.equals("generate") && !key.equals("n")) {
                printCode = printCode.replaceAll(key, value);
            }
        }
        formateCode = printCode;
        return formateCode;
    }

    public static Map<String, String> stringToMap(String s) {
        String key = "", value = "";
        Map<String, String> map = new HashMap<>();
        boolean b = true;
        s = s + "/";
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '/') {
                b = true;
                map.put(key, value);
                key = "";
                value = "";
            }
            if (s.charAt(i) == ':') {
                b = false;
            }
            if (s.charAt(i) != '/' && s.charAt(i) != ':' && s.charAt(i) != ' ') {
                if (b) {
                    key = key + s.charAt(i);
                } else {
                    value = value + s.charAt(i);

                }
            }
        }
        return map;
    }


}
