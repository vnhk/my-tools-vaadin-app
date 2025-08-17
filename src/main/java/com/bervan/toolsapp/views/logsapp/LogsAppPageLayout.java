package com.bervan.toolsapp.views.logsapp;

import com.bervan.common.MenuNavigationComponent;
import com.vaadin.flow.component.icon.VaadinIcon;

public final class LogsAppPageLayout extends MenuNavigationComponent {

    public LogsAppPageLayout(String currentRouteName) {
        super(currentRouteName);

        addButtonIfVisible(menuButtonsRow, LogItemsTableView.ROUTE_NAME, "Logs", VaadinIcon.HOME.create());
        addButtonIfVisible(menuButtonsRow, LogItemsTableView.ROUTE_NAME, "Trackers", VaadinIcon.HOME.create());

        add(menuButtonsRow);

    }
}
