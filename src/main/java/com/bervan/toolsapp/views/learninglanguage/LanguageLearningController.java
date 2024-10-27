package com.bervan.toolsapp.views.learninglanguage;

import com.bervan.core.model.BervanLogger;
import com.bervan.languageapp.TranslationRecord;
import com.bervan.languageapp.service.ExampleOfUsageService;
import com.bervan.languageapp.service.TextToSpeechService;
import com.bervan.languageapp.service.TranslationRecordService;
import com.bervan.languageapp.service.TranslatorService;
import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LanguageLearningController {
    private final TranslationRecordService translationRecordService;
    private final ExampleOfUsageService exampleOfUsageService;
    private final TextToSpeechService textToSpeechService;
    private final TranslatorService translationService;
    private final BervanLogger log;

    public LanguageLearningController(TranslationRecordService translationRecordService, ExampleOfUsageService exampleOfUsageService,
                                      TextToSpeechService textToSpeechService, TranslatorService translationService, BervanLogger log) {
        this.translationRecordService = translationRecordService;
        this.exampleOfUsageService = exampleOfUsageService;
        this.textToSpeechService = textToSpeechService;
        this.translationService = translationService;
        this.log = log;
    }

    @PostMapping(path = "/language-learning/translation")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addTranslation(@RequestBody TranslationRecordRequest request) {
        try {
            TranslationRecord record = new TranslationRecord();
            record.setDeleted(false);
            record.setFactor(1);
            record.setSourceText(request.englishText);
            record.setTextTranslation(request.polishText);

            if (request.generateExample) {
                List<String> exampleOfUsage = exampleOfUsageService.createExampleOfUsage(request.englishText);
                String examples = exampleOfUsage.toString();
                if (!examples.isBlank() && exampleOfUsage.size() > 0) {
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
                    String examplesTranslated = translationService.translate(examples);
                    record.setInSentenceTranslation(examplesTranslated);
                }
            }

            if (request.saveWithSound) {
                record.setTextSound(textToSpeechService.getTextSpeech(request.englishText));
                if (!Strings.isNullOrEmpty(record.getInSentence())) {
                    record.setInSentenceSound(textToSpeechService.getTextSpeech(record.getInSentence()));
                }
            }

            TranslationRecord saved = translationRecordService.save(record);

            return ResponseEntity.ok(saved.getId() + " saved.");
        } catch (Exception e) {
            log.error("Unable to save new translation item!", e);
            return ResponseEntity.badRequest().body("Unable to save new translation item!");
        }
    }

    @GetMapping(path = "/language-learning/translate")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> translate(@RequestParam String text) {
        return new ResponseEntity<>(translationService.translate(text), HttpStatus.OK);
    }
}
