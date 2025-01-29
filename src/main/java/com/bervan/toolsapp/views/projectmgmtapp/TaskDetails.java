package com.bervan.toolsapp.views.projectmgmtapp;

import com.bervan.projectmgmtapp.AbstractTaskDetails;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractTaskDetails.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractTaskDetails.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class TaskDetails extends AbstractTaskDetails {

}