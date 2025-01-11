package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.WordService;
import com.bervan.filestorage.model.Metadata;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
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
    private final TranslationRecordService translationRecordService;
    private final TranslatorService translatorService;
    private final ExampleOfUsageService exampleOfUsageService;

    public VideoPlayerView(VideoManager videoManager, BervanLogger logger, WordService wordService, TranslationRecordService translationRecordService, TranslatorService translatorService, ExampleOfUsageService exampleOfUsageService) {
        super(logger, videoManager);
        this.videoManager = videoManager;
        this.logger = logger;
        this.wordService = wordService;
        this.translationRecordService = translationRecordService;
        this.translatorService = translatorService;
        this.exampleOfUsageService = exampleOfUsageService;
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

        if(enSubtitle.isEmpty()) {
            logger.error("Could not find english subtitles based on provided video id!");
            return;
        }

        add(new EnglishInVideoNotLearned(wordService, translationRecordService, translatorService, exampleOfUsageService, logger, enSubtitle.get().getPath() + File.separator + enSubtitle.get().getFilename()));
    }
}
