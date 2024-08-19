package com.bervan.toolsapp.views.englishepub;

import com.bervan.englishtextstats.AbstractNotLearnedWordsView;
import com.bervan.englishtextstats.WordService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Not learned yet...")
@Route(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
public class NotLearnedWordsView extends AbstractNotLearnedWordsView {
    public NotLearnedWordsView(@Autowired WordService service) {
        super(service);
    }

}
