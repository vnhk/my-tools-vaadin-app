package com.bervan.toolsapp.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login")
public class LoginView extends Div {

    public LoginView() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        add(loginForm);
    }
}