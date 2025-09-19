package com.bervan.toolsapp.views.learninglanguage.es;

import com.bervan.languageapp.service.CrosswordService;
import com.bervan.languageapp.view.es.AbstractSpanishCrosswordView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractSpanishCrosswordView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class SpanishCrosswordView extends AbstractSpanishCrosswordView {
    public SpanishCrosswordView(CrosswordService crosswordService) {
        super(crosswordService);
    }
}
