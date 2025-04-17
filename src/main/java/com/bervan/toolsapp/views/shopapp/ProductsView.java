package com.bervan.toolsapp.views.shopapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.ProductSearchService;
import com.bervan.shstat.view.AbstractProductView;
import com.bervan.shstat.view.ProductViewService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProductView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProductsView extends AbstractProductView {

    public ProductsView(ProductViewService productViewService, ProductSearchService productSearchService, BervanLogger log) {
        super(productViewService, productSearchService, log);
    }
}
