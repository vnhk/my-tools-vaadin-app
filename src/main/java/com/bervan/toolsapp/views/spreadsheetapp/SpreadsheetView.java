package com.bervan.toolsapp.views.spreadsheetapp;

import com.bervan.spreadsheet.functions.SpreadsheetFunction;
import com.bervan.spreadsheet.view.AbstractSpreadsheetView;
import com.bervan.spreadsheet.service.SpreadsheetService;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = AbstractSpreadsheetView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class SpreadsheetView extends AbstractSpreadsheetView implements HasUrlParameter<String> {

    public SpreadsheetView(SpreadsheetService service, BervanLogger logger, List<? extends SpreadsheetFunction> spreadsheetFunctions) {
        super(service, logger, spreadsheetFunctions);
    }
}