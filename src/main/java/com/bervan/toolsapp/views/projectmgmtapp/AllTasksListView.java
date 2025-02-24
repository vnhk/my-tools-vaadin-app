package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.views.AbstractAllTasksListView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractAllTasksListView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class AllTasksListView extends AbstractAllTasksListView {

    public AllTasksListView(BaseService<UUID, Task> service, BervanLogger log) {
        super(service, log);
    }
}