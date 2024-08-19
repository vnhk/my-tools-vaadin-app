package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.view.AbstractStartInterviewView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.bervan.toolsapp.views.MainLayout;

@PageTitle("Interview")
@Route(value = AbstractStartInterviewView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractStartInterviewView.ROUTE_NAME, layout = MainLayout.class)
public class StartInterviewView extends AbstractStartInterviewView {

    public StartInterviewView() {
        super();
    }

}
