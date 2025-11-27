package com.bervan.toolsapp.views.interview;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.interviewapp.view.AbstractInterviewQuestionsView;
import com.bervan.interviewapp.interviewquestions.InterviewQuestionService;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.bervan.toolsapp.views.MainLayout;

@Route(value = AbstractInterviewQuestionsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class InterviewQuestionsView extends AbstractInterviewQuestionsView {
    public InterviewQuestionsView(InterviewQuestionService questionService, BervanViewConfig bervanViewConfig) {
        super(questionService, bervanViewConfig);
    }

}
