package com.bervan.toolsapp.views.learninglanguage.en;

import com.bervan.languageapp.service.CrosswordService;
import com.bervan.languageapp.view.en.AbstractEnglishCrosswordView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractEnglishCrosswordView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class EnglishCrosswordView extends AbstractEnglishCrosswordView {
    public EnglishCrosswordView(CrosswordService crosswordService) {
        super(crosswordService);
    }
}
