package com.bervan.toolsapp.views.filestorage;

import com.bervan.common.model.BervanLogger;
import com.bervan.filestorage.service.FileServiceManager;
import com.bervan.filestorage.service.LoadStorageAndIntegrateWithDB;
import com.bervan.filestorage.view.AbstractFileStorageView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Route(value = AbstractFileStorageView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractFileStorageView.ROUTE_NAME, layout = MainLayout.class)
public class FileStorageView extends AbstractFileStorageView {

    public FileStorageView(@Autowired FileServiceManager service,
                           @Value("${spring.servlet.multipart.max-file-size}") String maxFileSize,
                           LoadStorageAndIntegrateWithDB loadStorageAndIntegrateWithDB,
                           BervanLogger logger) {
        super(service, maxFileSize, loadStorageAndIntegrateWithDB, logger);
    }

}
