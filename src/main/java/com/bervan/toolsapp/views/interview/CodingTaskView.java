package com.bervan.toolsapp.views.interview;

import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.pocketitem.CodingTaskService;
import com.bervan.interviewapp.view.AbstractCodingTaskView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractCodingTaskView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractCodingTaskView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class CodingTaskView extends AbstractCodingTaskView {
    public CodingTaskView(CodingTaskService service, BervanLogger log) {
        super(service, log);
    }

}
