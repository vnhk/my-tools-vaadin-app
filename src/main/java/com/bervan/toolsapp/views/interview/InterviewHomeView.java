package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.view.AbstractInterviewHomeView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.bervan.toolsapp.views.MainLayout;

@PageTitle("Interview")
@Route(value = AbstractInterviewHomeView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractInterviewHomeView.ROUTE_NAME, layout = MainLayout.class)
public class InterviewHomeView extends AbstractInterviewHomeView {

    public InterviewHomeView() {
        super();
    }

}
