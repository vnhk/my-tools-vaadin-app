package com.bervan.toolsapp.views.shopapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.search.SearchService;
import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.service.ProductAlertService;
import com.bervan.shstat.service.ProductConfigService;
import com.bervan.shstat.view.AbstractProductAlertView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProductAlertView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProductAlertsView extends AbstractProductAlertView {

    public ProductAlertsView(ProductAlertService service, SearchService searchService, ProductConfigService productConfigService, BervanLogger log, BervanViewConfig bervanViewConfig) {
        super(service, productConfigService, searchService, log, bervanViewConfig);
    }
}
