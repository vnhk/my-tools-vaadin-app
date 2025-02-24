package com.bervan.toolsapp.views.interview;

import com.bervan.common.model.PersistableTableData;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.view.AbstractImportExportView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.bervan.toolsapp.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.UUID;

@Route(value = AbstractImportExportView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class ImportExportInterviewDataView extends AbstractImportExportView {

    public ImportExportInterviewDataView(List<BaseService<UUID, ? extends PersistableTableData<?>>> dataServices, BervanLogger logger) {
        super(dataServices, logger);
    }
}
