package com.bervan.toolsapp.views.shopapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.search.SearchService;
import com.bervan.shstat.service.ShopConfigService;
import com.bervan.shstat.view.AbstractShopConfigView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractShopConfigView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ShopConfigView extends AbstractShopConfigView {

    public ShopConfigView(ShopConfigService shopConfigService, SearchService searchService, BervanViewConfig bervanViewConfig) {
        super(shopConfigService, searchService, bervanViewConfig);
    }
}
