package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TextToSpeechService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
import com.bervan.languageapp.view.AbstractLearningAppHomeView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = AbstractLearningAppHomeView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractLearningAppHomeView.ROUTE_NAME, layout = MainLayout.class)
public class LearningAppHomeView extends AbstractLearningAppHomeView {

    public LearningAppHomeView(TranslationRecordService translatorRecordService,
                               ExampleOfUsageService exampleOfUsageService, TextToSpeechService textToSpeechService, TranslatorService translatorService) {
        super(translatorRecordService, exampleOfUsageService, textToSpeechService, translatorService);
    }

}
