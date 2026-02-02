package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.streamingapp.config.ProductionData;
import com.bervan.streamingapp.view.AbstractRemoteControlView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = AbstractRemoteControlView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class RemoteControlView extends AbstractRemoteControlView {

    public RemoteControlView(Map<String, ProductionData> streamingProductionData) {
        super(streamingProductionData);
    }
}
