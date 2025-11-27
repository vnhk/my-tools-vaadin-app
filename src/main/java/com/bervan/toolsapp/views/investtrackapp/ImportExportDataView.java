package com.bervan.toolsapp.views.investtrackapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.investtrack.model.WalletSnapshot;
import com.bervan.investtrack.view.AbstractImportExportData;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractImportExportData.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ImportExportDataView extends AbstractImportExportData {

    public ImportExportDataView(BaseService<UUID, WalletSnapshot> dataService, BervanViewConfig bervanViewConfig) {
        super(dataService, bervanViewConfig);
    }
}
