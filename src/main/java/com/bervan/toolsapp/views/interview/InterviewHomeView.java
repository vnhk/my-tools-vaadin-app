package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.view.AbstractInterviewHomeView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.bervan.toolsapp.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractInterviewHomeView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractInterviewHomeView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class InterviewHomeView extends AbstractInterviewHomeView {

    public InterviewHomeView() {
        super();
    }

}
