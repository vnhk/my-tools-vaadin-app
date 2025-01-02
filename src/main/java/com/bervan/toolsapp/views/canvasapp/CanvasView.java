package com.bervan.toolsapp.views.canvasapp;

import com.bervan.streamingapp.CanvasService;
import com.bervan.streamingapp.view.AbstractCanvasView;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractCanvasView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractCanvasView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class CanvasView extends AbstractCanvasView {

    public CanvasView(CanvasService service, BervanLogger log) {
        super(service, log);
    }
}
