import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;


public class DroidEyeComponent implements ApplicationComponent {

    public DroidEyeComponent(){

    }

    @Override
    public void initComponent() {
        System.out.println("DroidPatrol is On >>>");
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
                                        "Quote of the day",
                                        "Find your data leak during development!!! Arabin",
                                        NotificationType.ERROR,
                                        null)),
                        ModalityState.NON_MODAL);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "DroidEyeComponent";
    }
}
