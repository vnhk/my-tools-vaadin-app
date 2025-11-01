package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.investtrack.service.WalletService;
import com.bervan.investtrack.view.dashboards.AbstractWalletsDashboardView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractWalletsDashboardView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class WalletsDashboardView extends AbstractWalletsDashboardView {
    public WalletsDashboardView(WalletService walletService) {
        super(walletService);
    }
}
