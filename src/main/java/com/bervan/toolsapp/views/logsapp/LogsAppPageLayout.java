package com.bervan.toolsapp.views.logsapp;

import com.bervan.common.MenuNavigationComponent;
import com.bervan.pocketapp.view.AbstractAllPocketItemsView;
import com.bervan.pocketapp.view.AbstractPocketView;

public final class LogsAppPageLayout extends MenuNavigationComponent {

    public LogsAppPageLayout(String currentRouteName) {
        super(currentRouteName);

        addButtonIfVisible(menuButtonsRow, LogItemsTableView.ROUTE_NAME, "Logs");

        add(menuButtonsRow);

    }
}
