package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.streamingapp.VideoManager;
import com.bervan.streamingapp.view.AbstractVideoListView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractVideoListView.ROUTE_NAME, layout = MainLayout.class)

@RolesAllowed({"USER", "STREAMING"})
public class VideoListView extends AbstractVideoListView {

    public VideoListView(VideoManager videoManager, BervanLogger logger) {
        super(logger, videoManager);
    }
}
