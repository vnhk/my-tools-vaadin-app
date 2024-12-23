package com.bervan.toolsapp.views.canvasapp;

import com.bervan.canvasapp.CanvasService;
import com.bervan.canvasapp.view.AbstractCanvasPagesView;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractCanvasPagesView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractCanvasPagesView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class CanvasPagesView extends AbstractCanvasPagesView {

    public CanvasPagesView(CanvasService service, BervanLogger log) {
        super(service, log);
    }
}
