package com.bervan.toolsapp.views.lowcode;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.lowcode.AbstractLowCodeGeneratorView;
import com.bervan.lowcode.LowCodeClass;
import com.bervan.lowcode.LowCodeClassDetailsService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = AbstractLowCodeGeneratorView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class LowCodeGeneratorView extends AbstractLowCodeGeneratorView {

    public LowCodeGeneratorView(BaseService<UUID, LowCodeClass> service, BervanViewConfig bervanViewConfig, BervanLogger bervanLogger, LowCodeClassDetailsService lowCodeClassDetailsService) {
        super(service, lowCodeClassDetailsService, bervanViewConfig, bervanLogger);
    }
}
