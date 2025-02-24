package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.view.AbstractLearningView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractLearningView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class LearningAppLearningView extends AbstractLearningView {

    public LearningAppLearningView(TranslationRecordService translatorRecordService) {
        super(translatorRecordService);
    }

}
