package com.bervan.toolsapp.views.logsapp;

import com.bervan.common.AbstractTableView;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.Operator;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.user.UserRepository;
import com.bervan.core.model.BervanLogger;
import com.bervan.logging.LogEntity;
import com.bervan.logging.LogService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Set;

@Route(value = LogItemsTableView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class LogItemsTableView extends AbstractTableView<Long, LogEntity> {
    public static final String ROUTE_NAME = "logs-app/all-logs";
    private String appName = "";
    private final LogService logService;
    private final UserRepository userRepository;
    private ComboBox<String> logSelector;

    public LogItemsTableView(LogService logService, BervanLogger log, UserRepository userRepository) {
        super(new LogsAppPageLayout(ROUTE_NAME), logService, log, LogEntity.class);
        this.logService = logService;
        this.userRepository = userRepository;

        checkboxesColumnsEnabled = false;
        pageSize = 500;

        addClassName("logs-item-view");

        Set<String> appsName = logService.loadAppsName();
        logSelector = new ComboBox<>("Application:", appsName);
        if (!appsName.isEmpty()) {
            appName = appsName.iterator().next();
            logSelector.setValue(appName);
        }
        logSelector.addValueChangeListener(comboBoxStringComponentValueChangeEvent -> {
            appName = comboBoxStringComponentValueChangeEvent.getValue();
            this.loadData();
            this.refreshData();
        });

        contentLayout.add(logSelector);

        renderCommonComponents();
        addButton.setVisible(false);
    }

    @Override
    protected Grid<LogEntity> getGrid() {
        Grid<LogEntity> grid = new Grid<>(LogEntity.class, false);
        grid.addClassName("flex-grid");
        buildGridAutomatically(grid);
        for (Grid.Column<LogEntity> column : grid.getColumns()) {
            column.setAutoWidth(true).setFlexGrow(1)
                    .setClassNameGenerator(item -> "wrap-text-grid");
        }

        return grid;
    }

    @Override
    protected void customizePreLoad(SearchRequest request) {
        request.addCriterion("APP_NAME_EQ_CRITERION", Operator.OR_OPERATOR, LogEntity.class,
                "applicationName", SearchOperation.EQUALS_OPERATION, appName);

        request.addOwnerAccessCriteria(LogEntity.class, userRepository.findByUsername("COMMON_USER").get().getId());
    }

    @Override
    protected List<LogEntity> loadData() {
        List<LogEntity> logEntities = super.loadData();
        logEntities.forEach(e -> e.setFullLog(e.getFullLog()));
        return logEntities;
    }
}