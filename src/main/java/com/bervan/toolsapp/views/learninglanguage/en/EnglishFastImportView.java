package com.bervan.toolsapp.views.learninglanguage.en;

import com.bervan.common.search.SearchService;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TextToSpeechService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
import com.bervan.languageapp.view.en.AbstractEnglishFastImportView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractEnglishFastImportView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class EnglishFastImportView extends AbstractEnglishFastImportView {

    public EnglishFastImportView(TranslationRecordService translationRecordService, TextToSpeechService textToSpeechService, SearchService searchService, ExampleOfUsageService exampleOfUsageService, TranslatorService translatorService) {
        super(translationRecordService, textToSpeechService, searchService, exampleOfUsageService, translatorService);
    }
}
