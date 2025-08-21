package com.bervan.toolsapp.views;

import com.bervan.common.MenuNavigationComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import static com.bervan.toolsapp.views.SettingsView.ROUTE;

@Route(value = ROUTE, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class SettingsView extends VerticalLayout {
    public final static String ROUTE = "settings";

    public SettingsView() {
        ComboBox<String> themeSelect = new ComboBox<>("Select Theme");
        themeSelect.setItems("cyberpunk", "retro", "earth", "darkula", "ocean", "sunset");
        themeSelect.addValueChangeListener(event -> {
            String selectedTheme = event.getValue();
            if (selectedTheme != null) {
                setTheme(selectedTheme);
            }
        });

        add(new MenuNavigationComponent("") {}, themeSelect);
    }

    private void setTheme(String themeName) {
        getElement().executeJs("localStorage.setItem('theme', '" + themeName + "');")
                .then(e -> UI.getCurrent().getPage().reload());
    }
}