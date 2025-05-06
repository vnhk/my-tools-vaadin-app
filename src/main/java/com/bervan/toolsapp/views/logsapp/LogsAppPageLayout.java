package com.bervan.toolsapp.views.logsapp;

import com.bervan.common.MenuNavigationComponent;

public final class LogsAppPageLayout extends MenuNavigationComponent {

    public LogsAppPageLayout(String currentRouteName) {
        super(currentRouteName);

        addButtonIfVisible(menuButtonsRow, LogItemsTableView.ROUTE_NAME, "Logs");
        addButtonIfVisible(menuButtonsRow, LogItemsTableView.ROUTE_NAME, "Trackers");

        add(menuButtonsRow);

    }
}
