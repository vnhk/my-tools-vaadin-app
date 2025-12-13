package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.user.UserRepository;
import com.bervan.investments.recommendation.AbstractInvestmentRecommendationView;
import com.bervan.investments.recommendation.InvestmentRecommendationService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = InvestmentRecommendationView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class InvestmentRecommendationView extends AbstractInvestmentRecommendationView {
    public InvestmentRecommendationView(InvestmentRecommendationService service, BervanViewConfig bervanViewConfig, UserRepository userRepository) {
        super(service, bervanViewConfig, userRepository);
    }
}
