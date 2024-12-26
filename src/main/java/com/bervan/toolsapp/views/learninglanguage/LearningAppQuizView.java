package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.view.AbstractQuizView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractQuizView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractQuizView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class LearningAppQuizView extends AbstractQuizView {

    public LearningAppQuizView(TranslationRecordService translatorRecordService, ExampleOfUsageService exampleOfUsageService) {
        super(translatorRecordService, exampleOfUsageService);
    }

}
