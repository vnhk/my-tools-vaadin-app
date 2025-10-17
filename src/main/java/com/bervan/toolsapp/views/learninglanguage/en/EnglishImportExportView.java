package com.bervan.toolsapp.views.learninglanguage.en;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.core.model.BervanLogger;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.view.en.AbstractEnglishImportExportView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractEnglishImportExportView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class EnglishImportExportView extends AbstractEnglishImportExportView {

    public EnglishImportExportView(BervanLogger logger, TranslationRecordService translationRecordService, BervanViewConfig bervanViewConfig) {
        super(logger, translationRecordService, bervanViewConfig);
    }
}
