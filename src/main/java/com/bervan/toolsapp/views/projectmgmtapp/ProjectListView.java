package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.views.AbstractProjectListView;
import com.bervan.projectmgmtapp.views.AbstractTaskDetailsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractProjectListView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractProjectListView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProjectListView extends AbstractProjectListView {

    public ProjectListView(BaseService<UUID, Project> service, BervanLogger log) {
        super(service, log);
    }
}