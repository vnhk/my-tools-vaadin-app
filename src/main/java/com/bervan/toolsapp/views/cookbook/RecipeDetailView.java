package com.bervan.toolsapp.views.cookbook;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.cookbook.service.*;
import com.bervan.cookbook.view.AbstractRecipeDetailView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractRecipeDetailView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class RecipeDetailView extends AbstractRecipeDetailView {

    public RecipeDetailView(RecipeService recipeService,
                            IngredientService ingredientService,
                            ShoppingCartService shoppingCartService,
                            UnitConversionEngine unitConversionEngine,
                            BervanViewConfig bervanViewConfig) {
        super(recipeService, ingredientService, shoppingCartService,
                unitConversionEngine, bervanViewConfig);
    }
}
