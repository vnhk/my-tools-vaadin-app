package com.bervan.toolsapp.views.interview;

import com.bervan.interviewapp.view.AbstractInterviewQuestionsView;
import com.bervan.interviewapp.interviewquestions.InterviewQuestionService;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import com.bervan.toolsapp.views.MainLayout;

@Route(value = AbstractInterviewQuestionsView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractInterviewQuestionsView.ROUTE_NAME, layout = MainLayout.class)
public class InterviewQuestionsView extends AbstractInterviewQuestionsView {
    public InterviewQuestionsView(@Autowired
                                  InterviewQuestionService questionService) {
        super(questionService);
    }

}
