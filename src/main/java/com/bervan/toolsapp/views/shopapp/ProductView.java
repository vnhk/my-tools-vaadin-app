package com.bervan.toolsapp.views.shopapp;

import com.bervan.common.user.UserRepository;
import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.ProductBasedOnDateAttributesService;
import com.bervan.shstat.ProductSearchService;
import com.bervan.shstat.ProductService;
import com.bervan.shstat.repository.ProductRepository;
import com.bervan.shstat.tokens.ProductSimilarOffersService;
import com.bervan.shstat.view.AbstractProductView;
import com.bervan.shstat.view.ProductViewService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractProductView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ProductView extends AbstractProductView {

    public ProductView(ProductViewService productViewService,
                       ProductSearchService productSearchService,
                       ProductRepository productRepository,
                       BervanLogger log,
                       UserRepository userRepository,
                       ProductService productService,
                       ProductBasedOnDateAttributesService productBasedOnDateAttributesService,
                       ProductSimilarOffersService productSimilarOffersService) {
        super(productViewService, productSearchService, userRepository, productService,
                productSimilarOffersService, productRepository, productBasedOnDateAttributesService, log);
    }
}
