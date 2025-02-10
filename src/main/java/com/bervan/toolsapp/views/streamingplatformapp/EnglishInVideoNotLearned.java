package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.common.AbstractTableView;
import com.bervan.common.EmptyLayout;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.Word;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.languageapp.service.AddFlashcardService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class EnglishInVideoNotLearned extends AbstractTableView<UUID, Word> {
    protected HorizontalLayout dialogButtonsLayout;
    protected final AddFlashcardService addAsFlashcardService;
    protected String englishSubtitlesPath;
    public EnglishInVideoNotLearned(WordService service,
                                    AddFlashcardService addAsFlashcardService,
                                    BervanLogger log,
                                    String englishSubtitlesPath) {
        super(new EmptyLayout(), service, log, Word.class);
        this.addAsFlashcardService = addAsFlashcardService;
        this.englishSubtitlesPath = englishSubtitlesPath;
        renderCommonComponents();
        refreshData();
        contentLayout.remove(addButton);
    }

    @Override
    protected List<Word> loadData() {
        return ((WordService) service).loadNotKnownWords(englishSubtitlesPath);
    }

    @Override
    protected Grid<Word> getGrid() {
        Grid<Word> grid = new Grid<>(Word.class, false);
        grid.addColumn(new ComponentRenderer<>(word -> formatTextComponent(word.getTableFilterableColumnValue())))
                .setHeader("Name").setKey("name").setResizable(true);
        grid.addColumn(new ComponentRenderer<>(word -> formatTextComponent(String.valueOf(word.getCount()))))
                .setHeader("Count").setKey("count").setResizable(true)
                .setSortable(true).setComparator(Comparator.comparing(Word::getCount));

        grid.getElement().getStyle().set("--lumo-size-m", 100 + "px");

        removeUnSortedState(grid, 1);

        return grid;
    }

    @Override
    protected void newItemButtonClick() {
        throw new RuntimeException("Open dialog is invalid");
    }

    @Override
    protected void buildNewItemDialogContent(Dialog dialog, VerticalLayout dialogLayout, HorizontalLayout headerLayout) {
        throw new RuntimeException("Open dialog is invalid");
    }

    @Override
    protected void buildOnColumnClickDialogContent(Dialog dialog, VerticalLayout dialogLayout, HorizontalLayout headerLayout, String clickedColumn, Word item) {
        dialogButtonsLayout = new HorizontalLayout();

        TextArea field = new TextArea(clickedColumn);
        field.setWidth("100%");

        field.setValue(item.getTableFilterableColumnValue());

        Button saveButton = new Button("Mark as learned.");
        saveButton.addClassName("option-button");

        saveButton.addClickListener(e -> {
            data.remove(item);
            grid.getDataProvider().refreshAll();
            service.save(item);
            dialog.close();
        });

        dialogButtonsLayout.add(saveButton);

        dialogLayout.add(headerLayout, field, dialogButtonsLayout);
        Button addAsFlashcard = new Button("Add as flashcard.");
        addAsFlashcard.addClickListener(e -> {
            data.remove(item);
            grid.getDataProvider().refreshAll();
            service.save(item);

            addAsFlashcardService.addAsFlashcardAsync(item);
            dialog.close();
        });

        dialogButtonsLayout.add(addAsFlashcard);
    }
}
