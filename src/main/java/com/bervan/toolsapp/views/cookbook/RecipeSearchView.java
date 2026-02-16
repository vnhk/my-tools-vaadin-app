package com.bervan.toolsapp.views.cookbook;

import com.bervan.cookbook.service.RecipeMatchingEngine;
import com.bervan.cookbook.view.AbstractRecipeSearchView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractRecipeSearchView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class RecipeSearchView extends AbstractRecipeSearchView {

    public RecipeSearchView(RecipeMatchingEngine matchingEngine) {
        super(matchingEngine);
    }
}
