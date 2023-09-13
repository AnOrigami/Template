package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class InitNoteYml extends AnAction {
    public static Map<String, String> noteMap = new HashMap<>();

    @Override
    public  void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        //获取当前项目目录
        VirtualFile baseDir = project.getBaseDir();
        String fileName = "/ltfnote.yml";
        String filePath = "";
        if (baseDir != null) {
            filePath = baseDir.getPath() + fileName;
        }


        Map<String, String> defaultMap = null;
        Yaml yaml = new Yaml();
        try {
            //读取 YAML 文件并将其解析为 Map 对象
            FileInputStream inputStream = new FileInputStream(filePath);
            Map<String, String> map = yaml.load(inputStream);
            defaultMap = map;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        noteMap = defaultMap;

    }
}
