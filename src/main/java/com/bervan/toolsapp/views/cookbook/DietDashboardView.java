package com.bervan.toolsapp.views.cookbook;

import com.bervan.cookbook.service.DietDashboardService;
import com.bervan.cookbook.view.AbstractDietDashboardView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractDietDashboardView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
@Deprecated
public class DietDashboardView extends AbstractDietDashboardView {

    public DietDashboardView(DietDashboardService dashboardService) {
        super(dashboardService);
    }
}
