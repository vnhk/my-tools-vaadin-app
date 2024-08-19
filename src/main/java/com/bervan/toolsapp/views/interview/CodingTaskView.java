package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.codingtask.CodingTaskService;
import com.bervan.interviewapp.view.AbstractCodingTaskView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@PageTitle("Coding Tasks")
@Route(value = AbstractCodingTaskView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractCodingTaskView.ROUTE_NAME, layout = MainLayout.class)
public class CodingTaskView extends AbstractCodingTaskView {
    public CodingTaskView(@Autowired
                          CodingTaskService service) {
        super(service);
    }

}
