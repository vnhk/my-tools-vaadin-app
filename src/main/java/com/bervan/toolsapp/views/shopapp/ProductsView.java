package com.bervan.toolsapp.views.shopapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.SearchService;
import com.bervan.shstat.view.AbstractProductView;
import com.bervan.shstat.view.ProductViewService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = AbstractProductView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractProductView.ROUTE_NAME, layout = MainLayout.class)
public class ProductsView extends AbstractProductView {

    public ProductsView(ProductViewService productViewService, SearchService searchService, BervanLogger log) {
        super(productViewService, searchService, log);
    }
}
