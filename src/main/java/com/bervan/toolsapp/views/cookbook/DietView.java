package com.bervan.toolsapp.views.cookbook;

import com.bervan.cookbook.service.DietService;
import com.bervan.cookbook.service.IngredientService;
import com.bervan.cookbook.view.AbstractDietView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractDietView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class DietView extends AbstractDietView {

    public DietView(DietService dietService, IngredientService ingredientService) {
        super(dietService, ingredientService);
    }
}
