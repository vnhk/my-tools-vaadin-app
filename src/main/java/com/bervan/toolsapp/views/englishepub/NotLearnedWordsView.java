package com.bervan.toolsapp.views.englishepub;

import com.bervan.common.onevalue.OneValueService;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.AbstractNotLearnedWordsView;
import com.bervan.englishtextstats.EbookPathLayout;
import com.bervan.englishtextstats.Word;
import com.bervan.englishtextstats.WordService;
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

    public NotLearnedWordsView(OneValueService oneValueService, WordService service, BervanLogger log, AddFlashcardService addAsFlashcardService) {
        super(service, new EbookPathLayout(oneValueService, service), log);
        this.addAsFlashcardService = addAsFlashcardService;
    }

    @Override
    protected void buildOnColumnClickDialogContent(Dialog dialog, VerticalLayout dialogLayout, HorizontalLayout headerLayout, String clickedColumn, Word item) {
        super.buildOnColumnClickDialogContent(dialog, dialogLayout, headerLayout, clickedColumn, item);

        Button addAsFlashcard = new Button("Add as flashcard.");
        addAsFlashcard.addClickListener(e -> {
            refreshDataAfterUpdate();
            service.save(item);

            addAsFlashcardService.addAsFlashcard(item);
            dialog.close();
        });

        dialogButtonsLayout.add(addAsFlashcard);
    }
}
