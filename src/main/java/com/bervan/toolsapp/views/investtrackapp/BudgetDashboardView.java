package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.investments.recommendation.InvestmentRecommendationService;
import com.bervan.investtrack.service.CurrencyConverter;
import com.bervan.investtrack.service.WalletService;
import com.bervan.investtrack.service.recommendations.ShortTermRecommendationStrategy;
import com.bervan.investtrack.view.dashboards.AbstractBudgetDashboardView;
import com.bervan.toolsapp.views.BudgetView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = AbstractBudgetDashboardView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class BudgetDashboardView extends AbstractBudgetDashboardView {

    public BudgetDashboardView(CurrencyConverter currencyConverter, WalletService walletService, Map<String, ShortTermRecommendationStrategy> strategies, InvestmentRecommendationService recommendationService) {
        super(currencyConverter, walletService, strategies, recommendationService);
    }

    public static MainLayout.MenuItemInfo[] subMenu() {
        return new MainLayout.MenuItemInfo[]{
                new MainLayout.MenuItemInfo("Investments", "", InvestmentWalletsView.class),
                new MainLayout.MenuItemInfo("Budget", "", BudgetView.class)};
    }
}
