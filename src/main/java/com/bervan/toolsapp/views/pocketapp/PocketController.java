package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PermitAll
public class PocketController {
    private final PocketItemService pocketItemService;
    private final PocketService pocketService;
    private final BervanLogger log;
    @Value("${api.keys}")
    private List<String> API_KEYS = new ArrayList<>();

    public PocketController(PocketItemService pocketItemService, PocketService pocketService, BervanLogger log) {
        this.pocketItemService = pocketItemService;
        this.pocketService = pocketService;
        this.log = log;
    }

    @PostMapping(path = "/pocket/pocket-item")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addToPocket(@RequestBody PocketRequest request) {
        if (!this.API_KEYS.contains(request.getApiKey())) {
            throw new RuntimeException("INVALID ACCESS");
        }
        try {
            PocketItem pocketItem = new PocketItem();
            pocketItem.setContent(request.getContent());
            pocketItem.setDeleted(false);
            pocketItem.setSummary(request.getSummary());
            PocketItem saved = pocketItemService.save(pocketItem, request.getPocketName());

            return ResponseEntity.ok(saved.getId() + " saved.");
        } catch (Exception e) {
            log.debug("Request failed for pocket name: " + request.getPocketName());
            log.error("Unable to save new pocket item!", e);
            return ResponseEntity.badRequest().body("Unable to save new pocket item!");

        }
    }

    @PostMapping(path = "/pocket/get-pocket-names")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<String>> getPocketNames(@RequestBody PocketRequest request) {
        if (!this.API_KEYS.contains(request.getApiKey())) {
            throw new RuntimeException("INVALID ACCESS");
        }
        return new ResponseEntity<>(pocketService.load().stream().map(Pocket::getName).collect(Collectors.toList()), HttpStatus.OK);
    }
}
