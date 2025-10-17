package com.bervan.toolsapp.views.englishepub;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.service.ExtractedEbookTextService;
import com.bervan.englishtextstats.view.AbstractEbooksView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractEbooksView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class EbooksView extends AbstractEbooksView {

    public EbooksView(ExtractedEbookTextService service, BervanLogger log, BervanViewConfig bervanViewConfig) {
        super(service, log, bervanViewConfig);
    }
}
