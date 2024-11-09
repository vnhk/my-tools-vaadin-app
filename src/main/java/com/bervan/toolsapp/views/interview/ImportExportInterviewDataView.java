package com.bervan.toolsapp.views.interview;

import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.pocketitem.CodingTaskService;
import com.bervan.interviewapp.interviewquestions.InterviewQuestionService;
import com.bervan.common.onevalue.OneValueService;
import com.bervan.interviewapp.view.AbstractImportExportView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.bervan.toolsapp.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractImportExportView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractImportExportView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class ImportExportInterviewDataView extends AbstractImportExportView {
    public ImportExportInterviewDataView(CodingTaskService codingTaskService, OneValueService oneValueService, InterviewQuestionService interviewQuestionService,  BervanLogger logger) {
        super(codingTaskService, oneValueService, interviewQuestionService, logger);
    }

}
