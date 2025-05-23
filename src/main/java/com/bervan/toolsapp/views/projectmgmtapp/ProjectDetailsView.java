package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.service.ProjectService;
import com.bervan.projectmgmtapp.service.TaskService;
import com.bervan.projectmgmtapp.views.AbstractProjectDetailsView;
import com.bervan.projectmgmtapp.views.AbstractTaskDetailsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProjectDetailsView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class ProjectDetailsView extends AbstractProjectDetailsView {

    public ProjectDetailsView(ProjectService projectService, TaskService taskService, BervanLogger logger) {
        super(projectService, taskService, logger);
    }
}