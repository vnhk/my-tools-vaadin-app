package com.bervan.toolsapp.views.englishepub;

import com.bervan.englishtextstats.AbstractNotLearnedWordsView;
import com.bervan.englishtextstats.EpubPathLayout;
import com.bervan.englishtextstats.WordService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
public class NotLearnedWordsView extends AbstractNotLearnedWordsView {
    public NotLearnedWordsView(WordService service, EpubPathLayout epubPathLayout) {
        super(service, epubPathLayout);
    }

}
