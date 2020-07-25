package actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.io.*;
import java.util.Scanner;

public class Eye extends AnAction {
    private String soot_path = "";
    private String android_jar = "";
    String txt = null;
    NotNullLazyValue<NotificationGroup> NOTIFICATION_GROUP;

    public Eye() {
        super("Text _Boxes");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // test code to create hyper link and open file upon click on it
        showAHyperLink(e.getProject());
        // TODO: insert action logic here
        Project p = e.getProject();
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (txt == null)txt= Messages.showInputDialog(project, "Please Enter the Drive name" +
                " for analyzer.jar and android.jar !!!", "Library path", Messages.getQuestionIcon());
        final File baseDir = new File(txt+":/");
        final FindFile ff = new FindFile("analyzer.jar", baseDir, 6);
        final File f = ff.find();
        soot_path = f.toString();
        File file = new File(p.getBasePath()+"/SourcesAndSinks.txt");
        try {
            if (file.createNewFile()){
                FileWriter writer = new FileWriter(file);
                String source = Messages.showInputDialog(project, "Please Enter the Source name: ", "Source", Messages.getQuestionIcon());
                writer.write(source);
                writer.write("\r\n");
                String sink = Messages.showInputDialog(project, "Please Enter the Sink name: ", "Sink", Messages.getQuestionIcon());
                writer.write(sink);
                writer.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String debugApkPath = p.getBasePath() + "/app/build/outputs/apk/";
        final File direc = new File(debugApkPath);
        final FindFile f_apk = new FindFile("app-debug.apk",direc,6);
        final File apk = f_apk.find();
        final FindFile fff = new FindFile("android.jar", baseDir, 6);
        final File ffA = fff.find();
        android_jar = ffA.toString();
        if (apk == null){
            Messages.showMessageDialog(project, "Please build the apk first and try again !!!",
                    "Error", Messages.getInformationIcon());
            return;
        }
        analysisApk(p);
    }

    private void analysisApk(Project project) {
        String debugApkPath = project.getBasePath() + "/app/build/outputs/apk/debug/app-debug.apk";
        String apkAnalysisCMD = "java -jar " + soot_path + " -a " + debugApkPath + " -p " + android_jar + " -s " +
                project.getBasePath() + "/SourcesAndSinks.txt";
        System.out.println("Current working directory : " + project.getBasePath());
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd \"C:\\Program Files\" && " + apkAnalysisCMD);
        builder = builder.directory(new File(project.getBasePath()));
        builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String TAG_LEAKS = "leaks";
        String leaksText = "";
        StringBuilder text = new StringBuilder();
        Messages.showMessageDialog(project, "Analysis in on progress please wait !!!",
                "Processing !!", Messages.getInformationIcon());
        while (true) {
            try {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                text.append(line);
                text.append("\n");
                if (line.contains(TAG_LEAKS)) leaksText = line;
                System.out.println(line);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Scanner sc;
        StringBuilder sb = new StringBuilder();
        sc = new Scanner(text.toString());
        String source = null;
        while (sc.hasNextLine()) {
            source = sc.findInLine("was called with values from the following sources:");
            if (source != null) break;
            sc.nextLine();
        }
        if (source != null) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
                sb.append("\n");
            }
            showNotificationBallon("Analysis successfully completed !!!", leaksText + "\n" + sb);
            Messages.showMessageDialog(project, text.toString(), "Analysis Result", Messages.getInformationIcon());
        } else {
            showNotificationBallon("Analysis successfully completed !!!", leaksText);
            Messages.showMessageDialog(project, leaksText, "Analysis Result", Messages.getInformationIcon());
        }
    }

    private void showNotificationBallon(String title, String message) {
        NOTIFICATION_GROUP = new NotNullLazyValue<NotificationGroup>() {
            @NotNull
            @Override
            protected NotificationGroup compute() {
                return new NotificationGroup(
                        "Analysis Started > ",
                        NotificationDisplayType.STICKY_BALLOON,
                        true);
            }
        };

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> Notifications.Bus.notify(NOTIFICATION_GROUP.getValue()
                                .createNotification(
                                        title,
                                        message,
                                        NotificationType.ERROR,
                                        null)),
                        ModalityState.NON_MODAL);
    }

    private void showAHyperLink(Project project) {
        PsiFile[] fileList = FilenameIndex.getFilesByName(project, "MainActivity.java", GlobalSearchScope.projectScope(project));
        VirtualFile path = fileList[0].getVirtualFile();
        NOTIFICATION_GROUP = new NotNullLazyValue<NotificationGroup>() {
            @NotNull
            @Override
            protected NotificationGroup compute() {
                return new NotificationGroup(
                        "Analysis Started > ",
                        NotificationDisplayType.STICKY_BALLOON,
                        true);
            }
        };
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = NOTIFICATION_GROUP.getValue()
                    .createNotification("<html>Found a flaw", "<a href=\"" + path + "\" target=\"blank\">Click here</a> to open the class!</html>",
                            NotificationType.ERROR,
                            (notification1, hyperlinkEvent) -> {
                                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                    FileEditorManager.getInstance(project).openFile(path, true);
                                   /* if ("url".equals(hyperlinkEvent.getDescription())) {
                                        //VirtualFile file = LocalFileSystem.getInstance().findFileByPath();
                                        FileEditorManager.getInstance(project).openFile(path, true);
                                        //BrowserUtil.browse("http://youtrack.jetbrains.net/issue/IDEA-71270");
                                    }
                                    else {

                                        //File file = new File(filePath);
                                        //ShowFilePathAction.openFile(file);
                                    }*/
                                }
                            });
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            Notifications.Bus.notify(notification, projects[0]);
        });
    }
}