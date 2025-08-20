package com.bervan.toolsapp.views;

import com.bervan.common.AbstractPageNotFoundError;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route(value = "/application-error", layout = MainLayout.class)
public class ErrorPage extends AbstractPageNotFoundError {
}
