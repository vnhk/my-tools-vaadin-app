package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.canvasapp.VideoManager;
import com.bervan.canvasapp.view.AbstractVideoPlayerView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route(value = AbstractVideoPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractVideoPlayerView.ROUTE_NAME, layout = MainLayout.class)
@PermitAll
public class VideoPlayerView extends AbstractVideoPlayerView {

    public VideoPlayerView(VideoManager videoManager, BervanLogger logger) {
        super(logger, videoManager);
    }
}
