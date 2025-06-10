package com.bervan.toolsapp.views.logsapp;

import com.bervan.common.AbstractTableView;
import com.bervan.common.BervanButton;
import com.bervan.common.BervanButtonStyle;
import com.bervan.common.BervanComboBox;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private boolean showLastPage = true;
    private final BervanButton defaultLastHour1Button = new BervanButton("Last 1h", click -> {
        try {
            filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                    .get("FROM").setValue(LocalDateTime.now().minusHours(1));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        showLastPage = true;
        refreshTable.click();
    }, BervanButtonStyle.PRIMARY);
    private final HorizontalLayout buttonsWithDateFilters = new HorizontalLayout();
    private boolean initSearch = true;

    public LogItemsTableView(LogService logService, BervanLogger log, UserRepository userRepository) {
        super(new LogsAppPageLayout(ROUTE_NAME), logService, log, LogEntity.class);
        this.logService = logService;
        this.userRepository = userRepository;

        createFilterButtons();

        checkboxesColumnsEnabled = false;
        pageSize = 500;
        addClassName("logs-item-view");

        Set<String> appsName = logService.loadAppsName();
        logSelector = new BervanComboBox<>(appsName);
        if (!appsName.isEmpty()) {
            appName = appsName.iterator().next();
            logSelector.setValue(appName);
        }
        logSelector.addValueChangeListener(comboBoxStringComponentValueChangeEvent -> {
            appName = comboBoxStringComponentValueChangeEvent.getValue();
            this.showLastPage = true;
            this.loadData();
            this.refreshData();
        });

        renderCommonComponents();
        addButton.setVisible(false);

        topLayout.add(logSelector);
        filtersLayout.removeFilterableFields("fullLog");
        filtersLayout.addFilterableFields("message");
        filtersLayout.addFilterableFields("className");
        filtersLayout.addFilterableFields("methodName");
        topLayout.add(buttonsWithDateFilters);

        defaultLastHour1Button.click();
    }

    private void createFilterButtons() {
        buttonsWithDateFilters.add(new BervanButton("Last 5m", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusMinutes(5));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 10m", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusMinutes(10));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 30m", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusMinutes(30));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));
        buttonsWithDateFilters.add(defaultLastHour1Button);

        buttonsWithDateFilters.add(new BervanButton("Last 2h", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusHours(2));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 6h", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusHours(6));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 24h", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusHours(24));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 3d", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusHours(24 * 3));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));

        buttonsWithDateFilters.add(new BervanButton("Last 7d (max)", click -> {
            try {
                filtersLayout.getDateTimeFiltersMap().get(LogEntity.class.getDeclaredField("timestamp"))
                        .get("FROM").setValue(LocalDateTime.now().minusHours(24 * 7));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            showLastPage = true;
            super.refreshTable.click();
        }));
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
    protected List<String> getFieldsToFetchForTable() {
        List<String> columnsToFetchForTable = super.getFieldsToFetchForTable();
        columnsToFetchForTable.remove("fullLog");
        columnsToFetchForTable.add("lineNumber");
        columnsToFetchForTable.add("className");
        columnsToFetchForTable.add("methodName");
        columnsToFetchForTable.add("message");
        return columnsToFetchForTable;
    }

    @Override
    protected void customizePreLoad(SearchRequest request) {
        request.addCriterion("APP_NAME_EQ_CRITERION", Operator.OR_OPERATOR, LogEntity.class,
                "applicationName", SearchOperation.EQUALS_OPERATION, appName);

        request.setAddOwnerCriterion(false);
        sortField = "timestamp";
        sortDirection = SortDirection.ASCENDING;

        if (showLastPage) {
            allFound = countAll(request, new ArrayList<>());
            maxPages = (int) Math.ceil((double) allFound / pageSize);
            pageNumber = maxPages - 1;
            if (pageNumber < 0) {
                pageNumber = 0;
            }
            showLastPage = false;
        }
    }

    @Override
    protected void refreshData() {
        if (initSearch) {
            initSearch = false;
            return;
        }
        super.refreshData();
    }

    @Override
    protected List<LogEntity> loadData() {
        if (initSearch) {
            return new ArrayList<>();
        }
        List<LogEntity> logEntities = super.loadData();
        logEntities.forEach(e -> e.setFullLog(e.getFullLog()));
        return logEntities;
    }
}