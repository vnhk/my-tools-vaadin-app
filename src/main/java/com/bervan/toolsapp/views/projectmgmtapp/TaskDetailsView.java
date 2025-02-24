package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.service.TaskService;
import com.bervan.projectmgmtapp.views.AbstractTaskDetailsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractTaskDetailsView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class TaskDetailsView extends AbstractTaskDetailsView {

    public TaskDetailsView(TaskService taskService, BervanLogger logger) {
        super(taskService, logger);
    }
}