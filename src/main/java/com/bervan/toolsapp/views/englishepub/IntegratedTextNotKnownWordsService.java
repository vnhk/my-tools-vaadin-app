package com.bervan.toolsapp.views.englishepub;

import com.bervan.common.search.SearchService;
import com.bervan.core.model.BervanLogger;
import com.bervan.englishtextstats.KnownWord;
import com.bervan.englishtextstats.service.ExtractedEbookTextRepository;
import com.bervan.englishtextstats.service.KnownWordRepository;
import com.bervan.englishtextstats.service.TextNotKnownWordsService;
import com.bervan.languageapp.service.TranslationRecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Primary
@Service
public class IntegratedTextNotKnownWordsService extends TextNotKnownWordsService {
    private final TranslationRecordService translationRecordService;

    public IntegratedTextNotKnownWordsService(TranslationRecordService translationRecordService,
                                              ExtractedEbookTextRepository extractedEbookTextRepository,
                                              KnownWordRepository knownWordRepository,
                                              SearchService searchService,
                                              BervanLogger logger,
                                              @Value("${file.service.storage.folder}") String pathToFileStorage) {
        super(extractedEbookTextRepository, knownWordRepository, searchService, logger, pathToFileStorage);
        this.translationRecordService = translationRecordService;
    }

    @Override
    public void loadIntoMemory() {
        super.loadIntoMemory();
        updateInMemoryWords(translationRecordService.load(Pageable.ofSize(1000000))
                .stream().map(e -> {
                    KnownWord k = new KnownWord();
                    k.setValue(e.getSourceText());
                    return k;
                }).collect(Collectors.toList()));
    }
}
