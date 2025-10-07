package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.investtrack.service.WalletService;
import com.bervan.investtrack.service.WalletSnapshotService;
import com.bervan.investtrack.view.AbstractWalletView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractWalletView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class WalletView extends AbstractWalletView {
    public WalletView(WalletService service, WalletSnapshotService walletSnapshotService, BervanLogger logger) {
        super(service, walletSnapshotService, logger);
    }
}
