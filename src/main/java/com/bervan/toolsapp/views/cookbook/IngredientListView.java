package com.bervan.toolsapp.views.cookbook;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.cookbook.service.IngredientService;
import com.bervan.cookbook.view.AbstractIngredientListView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractIngredientListView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class IngredientListView extends AbstractIngredientListView {

    public IngredientListView(IngredientService service, BervanViewConfig bervanViewConfig) {
        super(service, bervanViewConfig);
    }
}
