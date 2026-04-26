package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.service.AuthService;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/pocket-app/pockets")
public class PocketRestController {

    private final PocketService pocketService;

    public PocketRestController(PocketService pocketService) {
        this.pocketService = pocketService;
    }

    record PocketDto(UUID id, String name, Integer pocketSize, LocalDateTime creationDate, LocalDateTime modificationDate) {}

    record PocketCreateRequest(String name) {}

    @GetMapping
    public ResponseEntity<Page<PocketDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Set<Pocket> pockets = pocketService.load(PageRequest.of(page, size));
        List<PocketDto> dtos = pockets.stream()
                .map(p -> new PocketDto(p.getId(), p.getName(), p.getPocketSize(), p.getCreationDate(), p.getModificationDate()))
                .sorted(Comparator.comparing(PocketDto::name, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        int total = dtos.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        return ResponseEntity.ok(new PageImpl<>(dtos.subList(fromIndex, toIndex), PageRequest.of(page, size), total));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PocketDto> getById(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return pocketService.load(PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(p -> ResponseEntity.ok(new PocketDto(p.getId(), p.getName(), p.getPocketSize(), p.getCreationDate(), p.getModificationDate())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PocketDto> create(@RequestBody PocketCreateRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pocket pocket = new Pocket();
        pocket.setId(UUID.randomUUID());
        pocket.setName(req.name());
        pocket.setCreationDate(LocalDateTime.now());
        pocket.setModificationDate(LocalDateTime.now());
        pocket.setDeleted(false);
        pocket.addOwner(AuthService.getLoggedUser().get());
        Pocket saved = pocketService.save(pocket);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PocketDto(saved.getId(), saved.getName(), saved.getPocketSize(), saved.getCreationDate(), saved.getModificationDate()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PocketDto> update(@PathVariable UUID id, @RequestBody PocketCreateRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Pocket> match = pocketService.load(PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        Pocket pocket = match.get();
        pocket.setName(req.name());
        pocket.setModificationDate(LocalDateTime.now());
        Pocket saved = pocketService.save(pocket);
        return ResponseEntity.ok(new PocketDto(saved.getId(), saved.getName(), saved.getPocketSize(), saved.getCreationDate(), saved.getModificationDate()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Pocket> match = pocketService.load(PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        pocketService.delete(match.get());
        return ResponseEntity.noContent().build();
    }
}
