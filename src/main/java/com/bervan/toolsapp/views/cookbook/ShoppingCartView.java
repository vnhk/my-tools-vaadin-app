package com.bervan.toolsapp.views.cookbook;

import com.bervan.cookbook.service.ShoppingCartService;
import com.bervan.cookbook.service.UnitConversionEngine;
import com.bervan.cookbook.view.AbstractShoppingCartView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractShoppingCartView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ShoppingCartView extends AbstractShoppingCartView {

    public ShoppingCartView(ShoppingCartService shoppingCartService,
                            UnitConversionEngine unitConversionEngine) {
        super(shoppingCartService, unitConversionEngine);
    }
}
