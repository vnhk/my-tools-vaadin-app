package com.bervan.toolsapp.views.spreadsheetapp;

import com.bervan.common.AbstractPageView;
import com.bervan.common.service.AuthService;
import com.bervan.spreadsheet.functions.SpreadsheetFunction;
import com.bervan.spreadsheet.model.Cell;
import com.bervan.spreadsheet.model.HistorySpreadsheet;
import com.bervan.spreadsheet.model.Spreadsheet;
import com.bervan.spreadsheet.service.HistorySpreadsheetRepository;
import com.bervan.spreadsheet.service.SpreadsheetRepository;
import com.bervan.spreadsheet.utils.SpreadsheetUtils;
import com.bervan.toolsapp.views.MainLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = HTMLDynamicTablePoC.ROUTE_NAME + "/:name?", layout = MainLayout.class)
@PermitAll
public class HTMLDynamicTablePoC extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = "/spreadsheet-app/poc";

    @Autowired
    private SpreadsheetRepository spreadsheetRepository;
    @Autowired
    private HistorySpreadsheetRepository historySpreadsheetRepository;

    @Autowired
    private List<SpreadsheetFunction> spreadsheetFunctions;

    private Spreadsheet spreadsheetEntity;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Div tableHtml;
    private final Div historyHtml = new Div();
    private int rows;
    private int columns;
    private Cell[][] cells;
    private boolean historyShow = false;
    private List<HistorySpreadsheet> sorted = new ArrayList<>();

    // Map for quick access to cells by their ID
    private Map<String, Cell> cellMap = new HashMap<>();

    public HTMLDynamicTablePoC() {

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String name) {
        if (event.getRouteParameters().get("name").isEmpty()) {
            name = "default";
        } else {
            name = event.getRouteParameters().get("name").get();
        }
        init(name);
    }

    private void init(String name) {

        // Load or create Spreadsheet
        List<Spreadsheet> optionalEntity = spreadsheetRepository.findByNameAndDeletedFalseAndOwnersId(name, AuthService.getLoggedUserId());

        if (optionalEntity.size() > 0) {
            spreadsheetEntity = optionalEntity.get(0);
            String body = spreadsheetEntity.getBody();

            // Deserialize body to cells
            try {
                cells = objectMapper.readValue(body, Cell[][].class);
                rows = cells.length;
                columns = cells[0].length;

                // Rebuild cell map
                rebuildCellMap();

            } catch (IOException e) {
                e.printStackTrace();
                // Initialize default values on error
                rows = 2;
                columns = 10;
                initializeCells();
            }
        } else {
            spreadsheetEntity = new Spreadsheet(name);
            rows = 2;
            columns = 10;
            initializeCells();
        }

        // Initial table
        tableHtml = new Div();
        tableHtml.getElement().setProperty("innerHTML", buildTable(columns, rows, cells));

        // Refresh all functions before building the table
        refreshAllFunctions();

        // Create the MenuBar
        MenuBar menuBar = new MenuBar();

        // File menu
        MenuItem fileMenu = menuBar.addItem("File");
        fileMenu.addClassName("option-button");
        fileMenuOptions(fileMenu);

        // Edit menu
        MenuItem editMenu = menuBar.addItem("Edit");
        editMenu.addClassName("option-button");
        editMenuOptions(editMenu);

        // Help menu
        MenuItem helpMenu = menuBar.addItem("Help");
        helpMenu.addClassName("option-button");

        helpMenuOptions(helpMenu);

        // Add the MenuBar and table to the layout
        add(menuBar, (tableHtml), new Hr(), historyHtml);

        // Refresh the table
        refreshTable();
    }

    private void helpMenuOptions(MenuItem helpMenu) {
        SubMenu helpSubMenu = helpMenu.getSubMenu();
        MenuItem showFunctionsItem = helpSubMenu.addItem("Show Available Functions", event -> {
            showAvailableFunctionsModal();
        });
    }

    private void editMenuOptions(MenuItem editMenu) {
        SubMenu editSubMenu = editMenu.getSubMenu();

        MenuItem addRowItem = editSubMenu.addItem("Add Row", event -> {
            rows++;
            updateCellsArray();
            refreshTable();
        });

        MenuItem addColumnItem = editSubMenu.addItem("Add Column", event -> {
            columns++;
            updateCellsArray();
            refreshTable();
        });

        MenuItem refreshTableItem = editSubMenu.addItem("Refresh Table", event -> {
            refreshTable();
            showSuccessNotification("Table refreshed");
        });
    }

    private void fileMenuOptions(MenuItem fileMenu) {
        SubMenu fileSubMenu = fileMenu.getSubMenu();

        MenuItem saveItem = fileSubMenu.addItem("Save", event -> {
            try {
                String body = objectMapper.writeValueAsString(cells);
                spreadsheetEntity.setBody(body);
                spreadsheetRepository.save(spreadsheetEntity);
                reloadHistory();
                Notification.show("Table saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                Notification.show("Failed to save table.");
            }
        });

        MenuItem copyTableItem = fileSubMenu.addItem("Copy Table", event -> {
            showCopyTableDialog();
        });

        MenuItem showHistory = fileSubMenu.addItem("History", event -> {
            historyShow = !historyShow;
            if (historyShow) {
                reloadHistory();
                if (sorted.size() > 0) {
                    showHistoryTable(0);
                }
            }
        });
    }

    private void showHistoryTable(int historyIndex) {
        //mark as red all different values compared to current
        HistorySpreadsheet historySpreadsheet = sorted.get(historyIndex);
        String tableHTML = "";
        try {
            Cell[][] historyCells = objectMapper.readValue(historySpreadsheet.getBody(), Cell[][].class);
            int historyRows = cells.length;
            int historyColumns = cells[0].length;
            tableHTML = buildTable(historyColumns, historyRows, historyCells);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorNotification("Could not show history change!");
        }

        String headerHTML = historySpreadsheet.getUpdateDate().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Div historyContent = new Div();
        historyContent.getElement().setProperty("innerHTML", headerHTML + "<br>" + tableHTML);

        // Create navigation buttons
        Button leftButton = new Button("Previous");
        leftButton.setVisible(historyIndex > 0); // Disable if at the first history
        leftButton.addClickListener(event -> showHistoryTable(historyIndex - 1));
        leftButton.addClassName("option-button");

        Button rightButton = new Button("Next");
        rightButton.addClassName("option-button");
        rightButton.setVisible(historyIndex < sorted.size() - 1); // Disable if at the last history
        rightButton.addClickListener(event -> showHistoryTable(historyIndex + 1));

        // Add buttons to the layout
        HorizontalLayout buttonContainer = new HorizontalLayout();
        buttonContainer.add(leftButton, rightButton);

        // Remove old content and re-add new components
        historyHtml.getElement().removeAllChildren();
        historyHtml.getElement().appendChild(historyContent.getElement(), new Hr().getElement(), buttonContainer.getElement());
    }

    private void reloadHistory() {
        List<HistorySpreadsheet> history = historySpreadsheetRepository.findAllByHistoryOwnerId(spreadsheetEntity.getId());
        sorted = history.stream().sorted(Comparator.comparing(HistorySpreadsheet::getUpdateDate).reversed()).collect(Collectors.toList());
    }

    private void refreshTable() {
        tableHtml.getElement().setProperty("innerHTML", buildTable(columns, rows, cells));

        // Listen for changes in the cells
        getElement().executeJs("const table = this.querySelector('table');" + "table.addEventListener('focusin', event => {" + "   const cell = event.target;" +
                "   if (cell.hasAttribute('contenteditable')) {" +
                "       const id = cell.id;" +
                "       $0.$server.cellFocusIn(id);" +
                "   }" +
                "});" +
                "table.addEventListener('focusout', event => {" +
                "   const cell = event.target;" +
                "   if (cell.hasAttribute('contenteditable')) {" +
                "       const id = cell.id;" +
                "       const value = cell.innerText;" +
                "       $0.$server.updateCellValue(id, value);" +
                "   }" +
                "});", getElement()
        );
    }

    private void initializeCells() {
        cells = new Cell[rows][columns];
        cellMap.clear();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = new Cell("", j, i);
                cells[i][j] = cell;
                cellMap.put(cell.cellId, cell);
            }
        }
    }

    private void updateCellsArray() {
        Cell[][] newCells = new Cell[rows][columns];

        // Copy existing values to the new array
        for (int i = 0; i < Math.min(cells.length, rows); i++) {
            for (int j = 0; j < Math.min(cells[0].length, columns); j++) {
                newCells[i][j] = cells[i][j];
            }
        }

        // Initialize new cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (newCells[i][j] == null) {
                    Cell cell = new Cell("", j, i);
                    newCells[i][j] = cell;
                    cellMap.put(cell.cellId, cell);
                } else {
                    // Update cell metadata
                    Cell cell = newCells[i][j];
                    cell.columnNumber = j;
                    cell.rowNumber = i;
                    cell.columnSymbol = getColumnName(j);
                    cell.cellId = cell.columnSymbol + cell.rowNumber; // Start row numbering from 0
                    cellMap.put(cell.cellId, cell);
                }
            }
        }

        cells = newCells;
    }

    private void rebuildCellMap() {
        cellMap.clear();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                // Update cell IDs to start row numbering from 0
                cell.columnNumber = j;
                cell.rowNumber = i;
                cell.columnSymbol = getColumnName(j);
                cell.cellId = cell.columnSymbol + cell.rowNumber;
                cellMap.put(cell.cellId, cell);
            }
        }
    }

    private String buildTable(int columns, int rows, Cell[][] cells) {
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table class='spreadsheet-table'>");

        // Build header row with column labels
        tableBuilder.append("<tr class='spreadsheet-header-row'>");
        tableBuilder.append("<th class='spreadsheet-row-number'>#</th>");
        for (int col = 0; col < columns; col++) {
            String columnLabel = getColumnName(col);
            String cellId = "header_" + col;
            tableBuilder.append("<th ")
                    .append("id='").append(cellId).append("' ")
                    .append("class='spreadsheet-header'>");
            tableBuilder.append(columnLabel);
            tableBuilder.append("</th>");
        }
        tableBuilder.append("</tr>");

        // Build data rows
        for (int row = 0; row < rows; row++) {
            tableBuilder.append("<tr>");

            // Row number cell
            tableBuilder.append("<td class='spreadsheet-row-number'>")
                    .append(row)
                    .append("</td>");

            for (int col = 0; col < columns; col++) {
                Cell cell = cells[row][col];
                String cellId = cell.cellId;

                tableBuilder.append("<td ")
                        .append("id='").append(cellId).append("' ")
                        .append("contenteditable='true' ")
                        .append("class='spreadsheet-cell'>");

                if (cell.isFunction) {
                    cell.buildFunction(cell.getFunctionValue());
                    // No need to calculate here, it's done elsewhere
                }
                String val = cell.value != null ? cell.value : "";
                tableBuilder.append(val);
                tableBuilder.append("</td>");
            }
            tableBuilder.append("</tr>");
        }

        tableBuilder.append("</table>");
        return tableBuilder.toString();
    }

    // Method to generate Excel-like column labels
    private String getColumnName(int columnIndex) {
        return SpreadsheetUtils.getColumnHeader(columnIndex);
    }

    @ClientCallable
    public void updateCellValue(String cellId, String value) {
        Cell cell = cellMap.get(cellId);
        if (cell != null) {
            // Update the cell's raw value
            cell.value = value;

            // If value starts with '=', it's a function
            if (value.startsWith("=")) {
                cell.isFunction = true;
                cell.buildFunction(value);
            } else {
                cell.isFunction = false;
                cell.functionName = null;
                cell.value = value;
            }

            // Recalculate dependent cells
            try {
                recalculateCell(cell);
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Error in formula calculation.");
            }

            // Update the cell in the client-side
            updateCellInClient(cell.cellId, cell.value);
        }
    }

    // New method to update a cell in the client-side
    private void updateCellInClient(String cellId, String value) {
        getElement().executeJs(
                "const cell = document.getElementById($0);" +
                        "if (cell) { cell.innerText = $1; }",
                cellId, value
        );
    }

    @ClientCallable
    public void cellFocusIn(String cellId) {
        Cell cell = cellMap.get(cellId);
        if (cell != null) {
            // If the cell is a function, display the function expression
            if (cell.isFunction) {
                getElement().executeJs(
                        "const cell = document.getElementById($0);" +
                                "if (cell) { cell.innerText = $1; }",
                        cellId, cell.getFunctionValue()
                );
            }
        }
    }

    private void recalculateCell(Cell cell) {
        if (cell.isFunction) {
            calculateFunctionValue(cell);
        }

        // Update the cell in the client-side
        updateCellInClient(cell.cellId, cell.value);

        // Now, find and update any cells that depend on this cell
        for (Cell dependentCell : getDependentCells(cell.cellId)) {
            recalculateCell(dependentCell);
        }
    }

    private void showAvailableFunctionsModal() {
        Dialog dialog = new Dialog();
        dialog.setWidth("80vw");

        Button okButton = new Button("Ok", e -> {
            dialog.close();
        });
        okButton.setClassName("option-button");

        // Add components to dialog
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(getDialogTopBarLayout(dialog));

        int i = 1;
        for (SpreadsheetFunction spreadsheetFunction : spreadsheetFunctions) {
            String name = spreadsheetFunction.getName();
            String info = spreadsheetFunction.getInfo();
            Span infoSpan = new Span();
            infoSpan.getElement().setProperty("innerHTML", info);
            verticalLayout.add(new Span(i + ") " + name), infoSpan);
            i++;
        }
        verticalLayout.add(okButton);
        dialog.add(verticalLayout);

        // Open the dialog
        dialog.open();
    }


    private void calculateFunctionValue(Cell cell) {
        String functionName = cell.functionName;
        Optional<? extends SpreadsheetFunction> first = spreadsheetFunctions.stream()
                .filter(e -> e.getName().equals(functionName))
                .findFirst();

        if (first.isPresent()) {
            cell.value = String.valueOf(first.get().calculate(cell.getRelatedCellsId(), getRows()));
        } else {
            cell.value = "NO FUNCTION";
        }
    }

    private List<List<Cell>> getRows() {
        List<List<Cell>> rowsList = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Cell> rowList = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                rowList.add(cells[i][j]);
            }
            rowsList.add(rowList);
        }
        return rowsList;
    }

    private Set<Cell> getDependentCells(String cellId) {
        Set<Cell> dependents = new HashSet<>();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                if (cell.isFunction && cell.getRelatedCellsId().contains(cellId)) {
                    dependents.add(cell);
                }
            }
        }
        return dependents;
    }

    private void refreshAllFunctions() {
        // Recalculate all functions and update them individually
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                if (cell.isFunction) {
                    try {
                        recalculateCell(cell);
                    } catch (Exception e) {
                        e.printStackTrace();
                        cell.value = "ERROR";
                        updateCellInClient(cell.cellId, cell.value);
                    }
                }
            }
        }
        // No need to refresh the table since cells have been updated individually
    }

    @ClientCallable
    public void showSuccessNotification(String message) {
        Notification.show(message);
    }

    @ClientCallable
    public void showErrorNotification(String message) {
        Notification.show(message);
    }

    // Method to show the copy table dialog
    private void showCopyTableDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("80vw");

        VerticalLayout layout = new VerticalLayout();

        // Generate the table content as plain text
        String tableText = generateTableText();

        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setHeight("400px");
        textArea.setValue(tableText);
        textArea.setReadOnly(true);

        // Instructions for the user
        Span instructions = new Span("Select all the text below and copy it to your clipboard:");

        // Add components to layout
        layout.add(instructions, textArea);

        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.setClassName("option-button");

        layout.add(closeButton);

        dialog.add(layout);
        dialog.open();
    }

    // Method to generate the table content as plain text
    private String generateTableText() {
        StringBuilder sb = new StringBuilder();

        // Build header row
        sb.append("#\t");
        for (int col = 0; col < columns; col++) {
            sb.append(getColumnName(col)).append("\t");
        }
        sb.append("\n");

        // Build data rows
        for (int row = 0; row < rows; row++) {
            sb.append(row).append("\t");
            for (int col = 0; col < columns; col++) {
                Cell cell = cells[row][col];
                String val = cell.value != null ? cell.value : "";
                sb.append(val).append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}