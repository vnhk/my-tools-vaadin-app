package com.bervan.toolsapp.views.shopapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.service.ProductSearchService;
import com.bervan.shstat.view.AbstractProductsView;
import com.bervan.shstat.view.ProductViewService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProductsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProductsView extends AbstractProductsView {

    public ProductsView(ProductViewService productViewService, ProductSearchService productSearchService, BervanLogger log) {
        super(productViewService, productSearchService, log);
    }
}
