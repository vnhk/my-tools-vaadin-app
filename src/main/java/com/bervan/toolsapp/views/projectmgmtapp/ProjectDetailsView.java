package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.projectmgmtapp.service.ProjectService;
import com.bervan.projectmgmtapp.service.TaskService;
import com.bervan.projectmgmtapp.views.AbstractProjectDetailsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProjectDetailsView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class ProjectDetailsView extends AbstractProjectDetailsView {

    public ProjectDetailsView(ProjectService projectService, TaskService taskService, BervanViewConfig bervanViewConfig) {
        super(projectService, taskService,  bervanViewConfig);
    }
}