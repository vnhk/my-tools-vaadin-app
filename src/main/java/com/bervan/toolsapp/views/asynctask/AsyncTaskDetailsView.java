package com.bervan.toolsapp.views.asynctask;

import com.bervan.asynctask.AsyncTask;
import com.bervan.asynctask.HistoryAsyncTask;
import com.bervan.common.service.BaseService;
import com.bervan.common.view.AbstractAsyncTaskDetails;
import com.bervan.core.model.BervanLogger;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;


@Route(value = AbstractAsyncTaskDetails.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed({"USER"})
public class AsyncTaskDetailsView extends AbstractAsyncTaskDetails {
    public AsyncTaskDetailsView(BaseService<UUID, AsyncTask> service, BaseService<UUID, HistoryAsyncTask> historyService, BervanLogger bervanLogger) {
        super(service, historyService, bervanLogger);
    }
}
