package com.bervan.toolsapp.views;

import com.bervan.common.AbstractPageView;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login")
public class LoginView extends AbstractPageView {

    public LoginView() {
        addClassName("login-page");
        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(loginForm);
    }
}