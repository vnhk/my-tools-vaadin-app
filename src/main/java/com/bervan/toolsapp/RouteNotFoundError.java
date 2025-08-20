package com.bervan.toolsapp;

import com.bervan.common.AbstractPageNotFoundError;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

@AnonymousAllowed
public class RouteNotFoundError extends VerticalLayout
        implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {

        AbstractPageNotFoundError abstractPageNotFoundError = new AbstractPageNotFoundError() {
        };
        add(abstractPageNotFoundError);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}