package com.bervan.toolsapp.views.englishepub;

import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.AbstractNotLearnedWordsView;
import com.bervan.englishtextstats.EpubPathLayout;
import com.bervan.englishtextstats.Word;
import com.bervan.englishtextstats.WordService;
import com.bervan.languageapp.TranslationRecord;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;

@Route(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
@RouteAlias(value = AbstractNotLearnedWordsView.ROUTE_NAME, layout = MainLayout.class)
public class NotLearnedWordsView extends AbstractNotLearnedWordsView {
    private final TranslationRecordService translationRecordService;
    private final TranslatorService translatorService;
    private final ExampleOfUsageService exampleOfUsageService;

    public NotLearnedWordsView(WordService service, EpubPathLayout epubPathLayout, TranslationRecordService translationRecordService, TranslatorService translatorService, ExampleOfUsageService exampleOfUsageService, BervanLogger log) {
        super(service, epubPathLayout, log);
        this.translationRecordService = translationRecordService;
        this.translatorService = translatorService;
        this.exampleOfUsageService = exampleOfUsageService;
    }

    @Override
    protected void buildOnColumnClickDialogContent(Dialog dialog, VerticalLayout dialogLayout, HorizontalLayout headerLayout, String clickedColumn, Word item) {
        super.buildOnColumnClickDialogContent(dialog, dialogLayout, headerLayout, clickedColumn, item);

        Button addAsFlashcard = new Button("Add as flashcard.");
        addAsFlashcard.addClickListener(e -> {
            data.remove(item);
            grid.getDataProvider().refreshAll();
            service.save(item);

            addAsFlashcard(item);
            dialog.close();
        });

        dialogButtonsLayout.add(addAsFlashcard);
    }

    protected void addAsFlashcard(Word item) {
        String name = item.getName();
        String translated = translatorService.translate(name);
        List<String> exampleOfUsage = exampleOfUsageService.createExampleOfUsage(name);
        String examples = exampleOfUsage.toString().replace("[", "").replace("]", "");

        TranslationRecord record = new TranslationRecord();
        record.setSourceText(name);
        record.setTextTranslation(translated);
        record.setFactor(1);
        if (!examples.isBlank()) {
            if (examples.length() > 250) {
                StringBuilder builder = new StringBuilder();
                for (String s : exampleOfUsage) {
                    if (builder.length() + s.length() + 1 > 250) {
                        break;
                    }
                    builder.append(s);
                    builder.append(",");
                }
                examples = builder.substring(0, builder.length() - 2);
            }

            record.setInSentence(examples);
            String examplesTranslated = translatorService.translate(examples);
            record.setInSentenceTranslation(examplesTranslated);
        }
        translationRecordService.save(record);

    }

}
