package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.views.AbstractProjectListView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractProjectListView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProjectListView extends AbstractProjectListView {

    public ProjectListView(BaseService<UUID, Project> service, BervanViewConfig bervanViewConfig) {
        super(service, bervanViewConfig);
    }
}