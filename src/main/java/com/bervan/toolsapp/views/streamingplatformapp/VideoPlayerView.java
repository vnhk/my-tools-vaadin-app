package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.common.service.AuthService;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.filestorage.model.Metadata;
import com.bervan.languageapp.service.AddFlashcardService;
import com.bervan.streamingapp.VideoManager;
import com.bervan.streamingapp.view.AbstractVideoPlayerView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = AbstractVideoPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractVideoPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class VideoPlayerView extends AbstractVideoPlayerView {
    private final VideoManager videoManager;
    private final BervanLogger logger;
    private final WordService wordService;
    private final AddFlashcardService addAsFlashcardService;

    public VideoPlayerView(VideoManager videoManager, BervanLogger logger, WordService wordService, AddFlashcardService addAsFlashcardService) {
        super(logger, videoManager);
        this.videoManager = videoManager;
        this.logger = logger;
        this.addAsFlashcardService = addAsFlashcardService;
        this.wordService = wordService;
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        super.setParameter(event, s);
        String videoId = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        List<Metadata> metadata = videoManager.loadById(videoId);

        if (metadata.size() != 1) {
            logger.error("Could not find file based on provided id!");
            return;
        }

        Metadata videoFolder = videoManager.getVideoFolder(metadata.get(0));
        List<Metadata> subtitles = videoManager.loadVideoDirectoryContent(videoFolder).get("SUBTITLES");
        if (subtitles == null) {
            logger.error("Could not find subtitles based on provided video id!");
            return;
        }

        Optional<Metadata> enSubtitle = subtitles.stream().filter(e -> e.getFilename().endsWith("en" + "." + e.getExtension()))
                .findFirst();

        if (enSubtitle.isEmpty()) {
            logger.error("Could not find english subtitles based on provided video id!");
            return;
        }

        if (AuthService.getUserRole().equals("ROLE_USER")) {
            add(new EnglishInVideoNotLearned(wordService, addAsFlashcardService, logger, enSubtitle.get().getPath() + File.separator + enSubtitle.get().getFilename()));
        }
    }
}
