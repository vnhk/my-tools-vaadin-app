package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.asynctask.AsyncTaskService;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.filestorage.model.BervanMockMultiPartFile;
import com.bervan.filestorage.model.Metadata;
import com.bervan.filestorage.service.FileServiceManager;
import com.bervan.languageapp.service.AddFlashcardService;
import com.bervan.logging.JsonLogger;
import com.bervan.streamingapp.VideoManager;
import com.bervan.streamingapp.config.ProductionData;
import com.bervan.streamingapp.config.StreamingConfigLoader;
import com.bervan.streamingapp.view.player.AbstractProductionPlayerView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Route(value = AbstractProductionPlayerView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER", "STREAMING"})
public class ProductionPlayerView extends AbstractProductionPlayerView {

    private final JsonLogger log = JsonLogger.getLogger(getClass(), "my-tools-app");
    private final FileServiceManager fileServiceManager;
    private final StreamingConfigLoader streamingConfigLoader;

    public ProductionPlayerView(VideoManager videoManager,
                                WordService wordService,
                                FileServiceManager fileServiceManager,
                                AsyncTaskService asyncTaskService,
                                AddFlashcardService addAsFlashcardService,
                                BervanViewConfig bervanViewConfig,
                                StreamingConfigLoader streamingConfigLoader,
                                Map<String, ProductionData> streamingProductionData) {
        super(videoManager, streamingProductionData);
        this.fileServiceManager = fileServiceManager;
        this.streamingConfigLoader = streamingConfigLoader;
    }

    @Override
    protected void addCustomComponents(String videoId, Metadata video) {
        add(new Hr());
        add(new H4("Upload Subtitle"));

        Checkbox reloadConfigCheckbox = new Checkbox("Reload Config");
        reloadConfigCheckbox.setValue(false);

        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".vtt", ".srt");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            try {
                String filename = event.getFileName();
                InputStream inputStream = buffer.getInputStream();
                String targetPath = video.getPath() + video.getFilename() + File.separator;

                BervanMockMultiPartFile mockFile = new BervanMockMultiPartFile(
                        filename, filename, null, inputStream
                );
                fileServiceManager.save(mockFile, "", targetPath);

                if (Boolean.TRUE.equals(reloadConfigCheckbox.getValue())) {
                    Map<String, ProductionData> newData = streamingConfigLoader.getStringProductionDataMap();
                    streamingProductionData.clear();
                    streamingProductionData.putAll(newData);
                    showSuccessNotification("Subtitle uploaded and config reloaded!");
                } else {
                    showSuccessNotification("Subtitle uploaded successfully!");
                }
            } catch (Exception e) {
                log.error("Failed to upload subtitle", e);
                showErrorNotification("Failed to upload subtitle!");
            }
        });

        VerticalLayout uploadSection = new VerticalLayout(upload, reloadConfigCheckbox);
        uploadSection.setSpacing(true);
        uploadSection.setPadding(false);
        add(uploadSection);
    }
}
