package com.bervan.toolsapp.views.shopapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.ProductSearchService;
import com.bervan.shstat.view.AbstractBestOffersView;
import com.bervan.shstat.view.DiscountsViewService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractBestOffersView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class BestOffersView extends AbstractBestOffersView {

    public BestOffersView(DiscountsViewService discountsViewService, ProductSearchService productSearchService, BervanLogger log) {
        super(discountsViewService, productSearchService, log);
    }
}
