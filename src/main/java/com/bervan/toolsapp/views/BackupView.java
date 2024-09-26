package com.bervan.toolsapp.views;

import com.bervan.common.AbstractPageView;
import com.bervan.toolsapp.service.WindowsDockerDatabaseBackupService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.context.annotation.Profile;

import static com.bervan.toolsapp.views.BackupView.ROUTE_NAME;

@Route(value = ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = ROUTE_NAME, layout = MainLayout.class)
public class BackupView extends AbstractPageView {
    public static final String ROUTE_NAME = "/backup";

    public BackupView(WindowsDockerDatabaseBackupService windowsDockerDatabaseBackupService) {
        super();
        Button dockerDatabaseBackup = new Button("Create docker database backup.");
        dockerDatabaseBackup.addClassName("option-button");

        dockerDatabaseBackup.addClickListener(event -> {
            if (windowsDockerDatabaseBackupService.backupDatabase()) {
                Notification.show("Backup created and saved in file storage.");
            } else {
                Notification.show("Backup creation failed! Check logs.");
            }
        });

        add(dockerDatabaseBackup);
    }
}
