package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.investtrack.model.Wallet;
import com.bervan.investtrack.view.AbstractWalletsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractWalletsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class WalletsView extends AbstractWalletsView {
    public WalletsView(BaseService<UUID, Wallet> service, BervanLogger logger) {
        super(service, logger);
    }
}
