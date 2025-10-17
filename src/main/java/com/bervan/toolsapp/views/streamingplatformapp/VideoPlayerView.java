package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.common.component.BervanButton;
import com.bervan.common.component.BervanButtonStyle;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.AuthService;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.filestorage.model.Metadata;
import com.bervan.filestorage.service.FileServiceManager;
import com.bervan.filestorage.view.UploadComponent;
import com.bervan.languageapp.service.AddFlashcardService;
import com.bervan.streamingapp.VideoManager;
import com.bervan.streamingapp.view.AbstractVideoPlayerView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Route(value = AbstractVideoPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class VideoPlayerView extends AbstractVideoPlayerView {
    private final VideoManager videoManager;
    private final BervanLogger logger;
    private final WordService wordService;
    private final FileServiceManager fileServiceManager;
    private final AddFlashcardService addAsFlashcardService;
    private final BervanViewConfig bervanViewConfig;

    public VideoPlayerView(VideoManager videoManager, BervanLogger logger, WordService wordService, FileServiceManager fileServiceManager, AddFlashcardService addAsFlashcardService, BervanViewConfig bervanViewConfig) {
        super(logger, videoManager);
        this.videoManager = videoManager;
        this.logger = logger;
        this.fileServiceManager = fileServiceManager;
        this.addAsFlashcardService = addAsFlashcardService;
        this.wordService = wordService;
        this.bervanViewConfig = bervanViewConfig;
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

        if (AuthService.getUserRole().equals("ROLE_USER")) {
            topLayout.add(new BervanButton("Upload subtitles", (e) -> {
                UploadComponent uploadComponent = new UploadComponent(fileServiceManager, metadata.get(0).getPath()) {
                    @Override
                    protected void postSaveActions() {
                        showSuccessNotification("Subtitles uploaded successfully!");
                        UI.getCurrent().refreshCurrentRoute(true);
                    }
                };
                uploadComponent.setSupportedFiles(".srt", ".vtt");
                uploadComponent.open();
            }, BervanButtonStyle.WARNING));
        }


        Metadata videoFolder = videoManager.getVideoFolder(metadata.get(0));
        List<Metadata> subtitles = videoManager.loadVideoDirectoryContent(videoFolder).get("SUBTITLES");
        if (subtitles == null) {
            logger.error("Could not find subtitles based on provided video id!");
            return;
        }

        Optional<Metadata> enSubtitle = videoManager.getSubtitle(VideoManager.EN, subtitles);

        if (enSubtitle.isEmpty()) {
            logger.error("Could not find english subtitles based on provided video id!");
            return;
        }

        if (AuthService.getUserRole().equals("ROLE_USER")) {
            add(new EnglishInVideoNotLearned(wordService, addAsFlashcardService, logger,
                    enSubtitle.get().getPath() + File.separator + enSubtitle.get().getFilename(), bervanViewConfig));
        }
    }
}
