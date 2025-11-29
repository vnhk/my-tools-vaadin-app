package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.asynctask.AsyncTaskService;
import com.bervan.investtrack.service.StockPriceReportService;
import com.bervan.investtrack.view.AbstractReportsRecommendationsView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractReportsRecommendationsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ReportRecommendationsView extends AbstractReportsRecommendationsView {

    protected ReportRecommendationsView(StockPriceReportService stockPriceReportService, AsyncTaskService asyncTaskService) {
        super(stockPriceReportService, asyncTaskService);
    }
}
