package com.bervan.toolsapp.views.interview;

import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.questionconfig.QuestionConfigService;
import com.bervan.interviewapp.view.AbstractQuestionConfigView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@Route(value = AbstractQuestionConfigView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractQuestionConfigView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class QuestionConfigView extends AbstractQuestionConfigView {
    public QuestionConfigView(@Autowired QuestionConfigService service, BervanLogger log) {
        super(service, log);
    }

}
