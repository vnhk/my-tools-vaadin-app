package com.bervan.toolsapp.views.interview;

import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.codingtask.CodingTaskService;
import com.bervan.interviewapp.view.AbstractCodingTaskView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractCodingTaskView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class CodingTaskView extends AbstractCodingTaskView {
    public CodingTaskView(CodingTaskService service, BervanLogger log) {
        super(service, log);
    }

}
