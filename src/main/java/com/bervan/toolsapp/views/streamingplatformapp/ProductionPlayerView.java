package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.asynctask.AsyncTaskService;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.filestorage.model.Metadata;
import com.bervan.filestorage.service.FileServiceManager;
import com.bervan.filestorage.view.UploadComponent;
import com.bervan.languageapp.service.AddFlashcardService;
import com.bervan.logging.JsonLogger;
import com.bervan.streamingapp.VideoManager;
import com.bervan.streamingapp.config.ProductionData;
import com.bervan.streamingapp.view.player.AbstractProductionPlayerView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = AbstractProductionPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class ProductionPlayerView extends AbstractProductionPlayerView {
    private static final String ROLE_USER = "ROLE_USER";

    private final JsonLogger log = JsonLogger.getLogger(getClass(), "my-tools-app");
    private final VideoManager videoManager;
    private final WordService wordService;
    private final FileServiceManager fileServiceManager;
    private final AsyncTaskService asyncTaskService;
    private final AddFlashcardService addAsFlashcardService;
    private final BervanViewConfig bervanViewConfig;

    public ProductionPlayerView(VideoManager videoManager,
                                WordService wordService,
                                FileServiceManager fileServiceManager,
                                AsyncTaskService asyncTaskService,
                                AddFlashcardService addAsFlashcardService,
                                BervanViewConfig bervanViewConfig,
                                Map<String, ProductionData> streamingProductionData) {
        super(videoManager, streamingProductionData);
        this.videoManager = videoManager;
        this.fileServiceManager = fileServiceManager;
        this.asyncTaskService = asyncTaskService;
        this.addAsFlashcardService = addAsFlashcardService;
        this.wordService = wordService;
        this.bervanViewConfig = bervanViewConfig;
    }

//    @Override
//    protected void addCustomNavigationButtons(String videoId, Metadata video) {
//        if (isUserRole()) {
//            addSubtitleUploadButton(video);
//        }
//    }

//    @Override
//    protected void addCustomComponents(String videoId, Metadata video) {
//        if (isUserRole()) {
//            addEnglishLearningComponent(videoId, video);
//        }
//    }
//
//    private boolean isUserRole() {
//        return ROLE_USER.equals(AuthService.getUserRole());
//    }
//
//    /**
//     * Adds button to upload subtitle files
//     */
//    private void addSubtitleUploadButton(Metadata video) {
//        BervanButton uploadButton = new BervanButton(
//                "Upload subtitles",
//                e -> openSubtitleUploadDialog(video),
//                BervanButtonStyle.WARNING
//        );
//        navigationBar.add(uploadButton);
//    }

    /**
     * Opens dialog for uploading subtitle files (.srt, .vtt)
     */
    private void openSubtitleUploadDialog(Metadata video) {
        UploadComponent uploadComponent = new UploadComponent(
                fileServiceManager,
                asyncTaskService,
                video.getPath()
        ) {
            @Override
            protected void postSaveActions() {
                showSuccessNotification("Subtitles uploaded successfully!");
                refreshPage();
            }
        };
        uploadComponent.setSupportedFiles(".srt", ".vtt");
        uploadComponent.open();
    }

    private void refreshPage() {
        UI.getCurrent().refreshCurrentRoute(true);
    }
//
//    /**
//     * Adds component showing English words from subtitles that haven't been learned yet
//     */
//    private void addEnglishLearningComponent(String videoId, Metadata video) {
//        try {
//            Optional<String> subtitlePath = findEnglishSubtitlePath(video);
//
//            if (subtitlePath.isEmpty()) {
//                log.warn("English subtitles not available for video: " + videoId);
//                return;
//            }
//
//            EnglishInVideoNotLearned learningComponent = new EnglishInVideoNotLearned(
//                    wordService,
//                    addAsFlashcardService,
//                    subtitlePath.get(),
//                    bervanViewConfig
//            );
//
//            add(learningComponent);
//
//        } catch (Exception e) {
//            log.error("Failed to add English learning component for video: " + videoId, e);
//            showErrorNotification("Could not load English learning features");
//        }
//    }

//    /**
//     * Finds the path to English subtitle file for the given video
//     */
//    private Optional<String> findEnglishSubtitlePath(Metadata video) {
//        try {
//            Metadata videoFolder = videoManager.getVideoFolder(video);
//            Map<String, Metadata> subtitlesByVideoId = videoManager.findMp4SubtitlesByVideoId(videoFolder.getId().toString(), streamingProductionData);
//
//
//            if (subtitlesByVideoId == null) {
//                log.info("No subtitles found for video");
//                return Optional.empty();
//            }
//
//            Metadata enSubtitle = subtitlesByVideoId.get(VideoManager.EN);
//            if (enSubtitle == null) {
//                log.info("English subtitle not found");
//                return Optional.empty();
//            }
//
//            String path = enSubtitle.getPath() + File.separator + enSubtitle.getFilename();
//            return Optional.of(path);
//
//        } catch (Exception e) {
//            log.error("Error finding English subtitle path", e);
//            return Optional.empty();
//        }
//    }
}