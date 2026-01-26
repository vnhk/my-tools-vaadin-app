package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.investtrack.service.WalletService;
import com.bervan.investtrack.view.AbstractWalletsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractWalletsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class InvestmentWalletsView extends AbstractWalletsView {
    public InvestmentWalletsView(WalletService service, BervanViewConfig bervanViewConfig) {
        super(service, bervanViewConfig);
    }
}
