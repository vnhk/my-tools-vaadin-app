package com.bervan.toolsapp.views.streamingplatformapp;

import com.bervan.common.view.EmptyLayout;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.Word;
import com.bervan.englishtextstats.service.WordService;
import com.bervan.englishtextstats.view.AbstractNotLearnedWordsBaseView;
import com.bervan.languageapp.service.AddFlashcardService;

import java.util.List;

public class EnglishInVideoNotLearned extends AbstractNotLearnedWordsBaseView {
    protected final AddFlashcardService addAsFlashcardService;
    protected String englishSubtitlesPath;

    public EnglishInVideoNotLearned(WordService service,
                                    AddFlashcardService addAsFlashcardService,
                                    BervanLogger log,
                                    String englishSubtitlesPath) {
        super(service, log, new EmptyLayout(), addAsFlashcardService, "EN");
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

}
