package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.investments.recommendation.InvestmentRecommendationService;
import com.bervan.investtrack.service.CurrencyConverter;
import com.bervan.investtrack.service.WalletService;
import com.bervan.investtrack.service.recommendations.ShortTermRecommendationStrategy;
import com.bervan.investtrack.view.dashboards.AbstractWalletsDashboardView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = AbstractWalletsDashboardView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class WalletsDashboardView extends AbstractWalletsDashboardView {

    public WalletsDashboardView(CurrencyConverter currencyConverter, WalletService walletService, Map<String, ShortTermRecommendationStrategy> strategies, InvestmentRecommendationService recommendationService) {
        super(currencyConverter, walletService, strategies, recommendationService);
    }

    public static MainLayout.MenuItemInfo[] subMenu() {
        return new MainLayout.MenuItemInfo[]{new MainLayout.MenuItemInfo("Wallets", "", WalletsView.class),
                new MainLayout.MenuItemInfo("Recommendations", "", ReportRecommendationsView.class),
                new MainLayout.MenuItemInfo("Alerts", "", StockAlertViewStock.class)};
    }
}
