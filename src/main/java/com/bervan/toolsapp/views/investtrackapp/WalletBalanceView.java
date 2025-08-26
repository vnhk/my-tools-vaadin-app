package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.investtrack.view.AbstractWalletBalanceView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractWalletBalanceView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class WalletBalanceView extends AbstractWalletBalanceView {
    public WalletBalanceView() {
        super();
    }
}
