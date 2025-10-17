package com.bervan.toolsapp.views.spreadsheetapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.spreadsheet.view.AbstractSpreadsheetsView;
import com.bervan.spreadsheet.model.Spreadsheet;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;


@Route(value = AbstractSpreadsheetsView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class SpreadsheetsView extends AbstractSpreadsheetsView {
    public SpreadsheetsView(BaseService<UUID, Spreadsheet> service, BervanLogger log, BervanViewConfig bervanViewConfig) {
        super(service, log, bervanViewConfig);
    }
}
