package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.onevalue.OneValueService;
import com.bervan.interviewapp.view.AbstractInterviewPlanView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@PageTitle("Interview Plan")
@Route(value = AbstractInterviewPlanView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractInterviewPlanView.ROUTE_NAME, layout = MainLayout.class)
public class InterviewPlanView extends AbstractInterviewPlanView {
    public InterviewPlanView(@Autowired OneValueService service) {
        super(service);
    }

}
