package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.view.AbstractPocketView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractPocketView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class PocketTableView extends AbstractPocketView {

    public PocketTableView(PocketService service, BervanLogger log, BervanViewConfig bervanViewConfig) {
        super(service, log, bervanViewConfig);
    }
}
