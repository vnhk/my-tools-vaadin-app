package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.core.model.BervanLogger;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TextToSpeechService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
import com.bervan.languageapp.view.AbstractLearningAppHomeView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractLearningAppHomeView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class LearningAppHomeView extends AbstractLearningAppHomeView {

    public LearningAppHomeView(TranslationRecordService translatorRecordService,
                               ExampleOfUsageService exampleOfUsageService, TextToSpeechService textToSpeechService, TranslatorService translatorService, BervanLogger log) {
        super(translatorRecordService, exampleOfUsageService, textToSpeechService, translatorService, log);
    }

}
