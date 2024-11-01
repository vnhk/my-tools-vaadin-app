package com.bervan.toolsapp.views.spreadsheetapp;

import com.bervan.spreadsheet.view.AbstractSpreadsheetView;
import com.bervan.spreadsheet.service.SpreadsheetService;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = AbstractSpreadsheetView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractSpreadsheetView.ROUTE_NAME, layout = MainLayout.class)
public class SpreadsheetView extends AbstractSpreadsheetView implements HasUrlParameter<String> {

    public SpreadsheetView(SpreadsheetService service, BervanLogger logger) {
        super(service, logger);
    }
}