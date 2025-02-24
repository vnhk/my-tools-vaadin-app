package com.bervan.toolsapp.views.interview;

import com.bervan.core.model.BervanLogger;
import com.bervan.interviewapp.view.AbstractInterviewQuestionsView;
import com.bervan.interviewapp.interviewquestions.InterviewQuestionService;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@Route(value = AbstractInterviewQuestionsView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed("USER")
public class InterviewQuestionsView extends AbstractInterviewQuestionsView {
    public InterviewQuestionsView(InterviewQuestionService questionService, BervanLogger log) {
        super(questionService, log);
    }

}
