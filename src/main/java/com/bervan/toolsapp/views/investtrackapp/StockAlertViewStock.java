package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.investtrack.model.StockPriceAlert;
import com.bervan.investtrack.view.AbstractStockPriceAlertsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractStockPriceAlertsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class StockAlertViewStock extends AbstractStockPriceAlertsView {
    public StockAlertViewStock(BaseService<UUID, StockPriceAlert> service, BervanLogger logger) {
        super(service, logger);
    }
}
