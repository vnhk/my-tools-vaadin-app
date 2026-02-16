package com.bervan.toolsapp.views.cookbook;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.cookbook.service.RecipeImportService;
import com.bervan.cookbook.service.RecipeService;
import com.bervan.cookbook.view.AbstractRecipeListView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractRecipeListView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class RecipeListView extends AbstractRecipeListView {

    public RecipeListView(RecipeService service, RecipeImportService recipeImportService,
                          BervanViewConfig bervanViewConfig) {
        super(service, recipeImportService, bervanViewConfig);
    }
}
