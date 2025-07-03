package com.bervan.toolsapp.views.canvasapp;

import com.bervan.streamingapp.CanvasService;
import com.bervan.streamingapp.view.AbstractCanvasPagesView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractCanvasPagesView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class CanvasPagesView extends AbstractCanvasPagesView {

    public CanvasPagesView(CanvasService service) {
        super(service);
    }
}
