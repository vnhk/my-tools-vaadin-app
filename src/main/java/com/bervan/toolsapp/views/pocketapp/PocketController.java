package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PocketController {
    private final PocketItemService pocketItemService;
    private final PocketService pocketService;
    private final BervanLogger log;

    public PocketController(PocketItemService pocketItemService, PocketService pocketService, BervanLogger log) {
        this.pocketItemService = pocketItemService;
        this.pocketService = pocketService;
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

    @GetMapping(path = "/pocket/pocket-names")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<String>> getPocketNames() {
        return new ResponseEntity<>(pocketService.load().stream().map(Pocket::getName).collect(Collectors.toList()), HttpStatus.OK);
    }
}
