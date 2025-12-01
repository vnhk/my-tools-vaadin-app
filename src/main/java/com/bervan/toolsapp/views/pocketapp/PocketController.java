package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.service.ApiKeyService;
import com.bervan.common.user.User;
import com.bervan.logging.JsonLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PermitAll
public class PocketController {
    private final JsonLogger log = JsonLogger.getLogger(getClass());
    private final PocketItemService pocketItemService;
    private final PocketService pocketService;

    private final ApiKeyService apiKeyService;
    @Value("${api.keys}")
    private List<String> API_KEYS = new ArrayList<>();

    public PocketController(PocketItemService pocketItemService, PocketService pocketService, ApiKeyService apiKeyService) {
        this.pocketItemService = pocketItemService;
        this.pocketService = pocketService;

        this.apiKeyService = apiKeyService;
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
            pocketItem.addOwner(apiKeyService.getUserByAPIKey(request.getApiKey()));

            PocketItem saved = pocketItemService.save(pocketItem, request.getPocketName(), apiKeyService.getUserByAPIKey(request.getApiKey()).getId());

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
        User userByAPIKey = apiKeyService.getUserByAPIKey(request.getApiKey());
        return new ResponseEntity<>(pocketService.loadForOwner(userByAPIKey).stream().map(Pocket::getName).collect(Collectors.toList()), HttpStatus.OK);
    }
}
