package com.bervan.toolsapp.views.shopapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.bervan.shstat.SearchService;
import com.bervan.shstat.view.AbstractBestOffersView;
import com.bervan.shstat.view.DiscountsViewService;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = AbstractBestOffersView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractBestOffersView.ROUTE_NAME, layout = MainLayout.class)
public class BestOffersView extends AbstractBestOffersView {


    public BestOffersView(DiscountsViewService discountsViewService, SearchService searchService, BervanLogger log) {
        super(discountsViewService, searchService, log);
    }
}
