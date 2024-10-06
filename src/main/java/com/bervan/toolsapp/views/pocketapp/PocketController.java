package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PocketController {
    private final PocketItemService pocketItemService;
    private final BervanLogger log;

    public PocketController(PocketItemService pocketItemService, BervanLogger log) {
        this.pocketItemService = pocketItemService;
        this.log = log;
    }

    @PostMapping(path = "/pocket/pocket-item")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addToPocket(@RequestBody PocketRequest request) {
        try {
            PocketItem pocketItem = new PocketItem();
            pocketItem.setContent(request.content);
            pocketItem.setDeleted(false);
            pocketItem.setSummary(request.summary);
            PocketItem saved = pocketItemService.save(pocketItem, request.pocketName);

            return ResponseEntity.ok(saved.getId() + " saved.");
        } catch (Exception e) {
            log.debug("Request failed for pocket name: " + request.pocketName);
            log.error("Unable to save new pocket item!", e);
            return ResponseEntity.badRequest().body("Unable to save new pocket item!");

        }
    }
}
