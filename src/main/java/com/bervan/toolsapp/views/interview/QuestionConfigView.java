package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.questionconfig.QuestionConfigService;
import com.bervan.interviewapp.view.AbstractQuestionConfigView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@PageTitle("Interview Plan")
@Route(value = AbstractQuestionConfigView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractQuestionConfigView.ROUTE_NAME, layout = MainLayout.class)
public class QuestionConfigView extends AbstractQuestionConfigView {
    public QuestionConfigView(@Autowired QuestionConfigService service) {
        super(service);
    }

}
