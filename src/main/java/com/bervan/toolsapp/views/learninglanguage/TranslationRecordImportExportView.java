package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.core.model.BervanLogger;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.view.AbstractImportExportView;
import com.bervan.languageapp.view.AbstractQuizView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractImportExportView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractImportExportView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class TranslationRecordImportExportView extends AbstractImportExportView {

    public TranslationRecordImportExportView(BervanLogger logger, TranslationRecordService translationRecordService, ExampleOfUsageService exampleOfUsageService) {
        super(logger, translationRecordService, exampleOfUsageService);
    }
}
