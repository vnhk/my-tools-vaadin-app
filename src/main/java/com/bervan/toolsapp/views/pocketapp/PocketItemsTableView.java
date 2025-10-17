package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.bervan.pocketapp.view.AbstractAllPocketItemsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractAllPocketItemsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class PocketItemsTableView extends AbstractAllPocketItemsView {

    public PocketItemsTableView(PocketItemService service, BervanLogger log, PocketService pocketService, BervanViewConfig bervanViewConfig) {
        super(service, pocketService, log, bervanViewConfig);
    }
}
