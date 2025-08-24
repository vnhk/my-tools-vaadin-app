package com.bervan.toolsapp;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;


@Theme(value = "my-theme")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Push
public class AppShell implements AppShellConfigurator {

}
