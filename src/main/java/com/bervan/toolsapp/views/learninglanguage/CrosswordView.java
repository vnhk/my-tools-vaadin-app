package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.languageapp.service.CrosswordService;
import com.bervan.languageapp.view.AbstractCrosswordView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractCrosswordView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class CrosswordView extends AbstractCrosswordView {
    public CrosswordView(CrosswordService crosswordService) {
        super();
    }
}
