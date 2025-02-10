package com.bervan.toolsapp.views.englishepub;

import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.Word;
import com.bervan.englishtextstats.service.ExtractedEbookTextRepository;
import com.bervan.englishtextstats.service.TextNotKnownWordsService;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.englishtextstats.view.AbstractNotLearnedWordsView;
import com.bervan.languageapp.service.AddFlashcardService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class NotLearnedWordsView extends AbstractNotLearnedWordsView {
    private final AddFlashcardService addAsFlashcardService;

    public NotLearnedWordsView(WordService service, TextNotKnownWordsService textNotKnownWordsService, ExtractedEbookTextRepository extractedEbookTextRepository, BervanLogger log, AddFlashcardService addAsFlashcardService) {
        super(service, extractedEbookTextRepository, textNotKnownWordsService, log);
        this.addAsFlashcardService = addAsFlashcardService;
    }

    @Override
    protected void buildOnColumnClickDialogContent(Dialog dialog, VerticalLayout dialogLayout, HorizontalLayout headerLayout, String clickedColumn, Word item) {
        super.buildOnColumnClickDialogContent(dialog, dialogLayout, headerLayout, clickedColumn, item);

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
