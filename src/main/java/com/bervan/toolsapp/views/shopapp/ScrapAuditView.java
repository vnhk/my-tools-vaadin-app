package com.bervan.toolsapp.views.shopapp;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.core.model.BervanLogger;
import com.bervan.shstat.service.ScrapAuditService;
import com.bervan.shstat.repository.ScrapAuditRepository;
import com.bervan.shstat.view.AbstractScrapAuditView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractScrapAuditView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class ScrapAuditView extends AbstractScrapAuditView {

    public ScrapAuditView(ScrapAuditService scrapAuditService, BervanLogger log, ScrapAuditRepository scrapAuditRepository, BervanViewConfig bervanViewConfig) {
        super(scrapAuditService, scrapAuditRepository, log, bervanViewConfig);
    }
}
