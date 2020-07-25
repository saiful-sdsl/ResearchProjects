package actions;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.Executors;

import static com.intellij.rt.execution.testFrameworks.ProcessBuilder.isWindows;

public class BuildApk extends AnAction {


    public BuildApk(){}

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        System.out.println(isWindows ? "This is a Windows machine." : "This is NOT a windows machine.");
        Project currentProject = anActionEvent.getProject();
        buildApk(currentProject);
    }

    public void getJarFilePath() {
        try {
            String path = BuildApk.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            System.out.println(decodedPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void getListDir() {
        String homeDirectory = System.getProperty("user.home");
        Process process = null;
        if (isWindows) {
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c dir %s", homeDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("sh -c ls %s", homeDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StreamGobblerLocal streamGobbler =
                new StreamGobblerLocal(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert exitCode == 0;
    }

    public void getListDirWithCustom() {
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "adb devices");
        } else {
            builder.command("sh", "-c", "ls");
        }
        builder.redirectErrorStream(true);
        builder.directory(new File(System.getProperty("user.home")));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StreamGobblerLocal streamGobbler =
                new StreamGobblerLocal(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert exitCode == 0;
    }

    public void buildApk(Project project) {
        System.out.println("Current working directory : " + project.getBasePath());
        try {
            Runtime.getRuntime().exec("cmd.exe /k cd \""+project.getBasePath()+"\" & start cmd.exe /c \"gradlew assembleDebug\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotificationBallon(String title, String message) {
        NotNullLazyValue<NotificationGroup> NOTIFICATION_GROUP = new NotNullLazyValue<NotificationGroup>() {
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
}