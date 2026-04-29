package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.service.AuthService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemRepository;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.bervan.common.config.EntityConfigValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/pocket-app/all-pocket-items")
public class PocketItemRestController {

    private final PocketItemService pocketItemService;
    private final PocketItemRepository pocketItemRepository;
    private final EntityConfigValidator validator;

    public PocketItemRestController(PocketItemService pocketItemService, PocketItemRepository pocketItemRepository, EntityConfigValidator validator) {
        this.pocketItemService = pocketItemService;
        this.pocketItemRepository = pocketItemRepository;
        this.validator = validator;
    }

    record ValidationErrorResponse(List<EntityConfigValidator.FieldError> errors) {}

    record PocketItemDto(
            UUID id, String summary, String content, Boolean encrypted,
            Integer orderInPocket, LocalDateTime creationDate, LocalDateTime modificationDate
    ) {}

    record PocketItemCreateRequest(
            String summary, String content, Boolean encrypted,
            Integer orderInPocket, String pocketName
    ) {}

    record PocketItemUpdateRequest(
            String summary, String content, Boolean encrypted, Integer orderInPocket
    ) {}

    record DecryptRequest(String password) {}

    record DecryptResponse(String content) {}

    @GetMapping
    public ResponseEntity<Page<PocketItemDto>> listByPocket(
            @RequestParam("pocket-name") String pocketName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderInPocket") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Set<PocketItem> items = pocketItemService.loadByPocketName(pocketName);
        List<PocketItemDto> dtos = items.stream()
                .map(i -> new PocketItemDto(i.getId(), i.getSummary(), i.isEncrypted() ? null : i.getContent(),
                        i.isEncrypted(), i.getOrderInPocket(), i.getCreationDate(), i.getModificationDate()))
                .sorted(buildComparator(sort, direction))
                .toList();
        int total = dtos.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        return ResponseEntity.ok(new PageImpl<>(dtos.subList(fromIndex, toIndex), PageRequest.of(page, size), total));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PocketItemDto> getById(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return findOwnedItem(id)
                .map(i -> ResponseEntity.ok(toDto(i)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PocketItemCreateRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<EntityConfigValidator.FieldError> errors = validator.validate("PocketItem", Map.of(
                "summary", req.summary() != null ? req.summary() : "",
                "content", req.content() != null ? req.content() : ""
        ));
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));

        PocketItem item = new PocketItem();
        item.setId(UUID.randomUUID());
        item.setSummary(req.summary());
        item.setContent(req.content() != null ? req.content() : "");
        item.setEncrypted(req.encrypted() != null && req.encrypted());
        item.setOrderInPocket(req.orderInPocket() != null ? req.orderInPocket() : 0);
        item.setDeleted(false);
        item.setCreationDate(LocalDateTime.now());
        item.setModificationDate(LocalDateTime.now());
        item.addOwner(AuthService.getLoggedUser().get());
        PocketItem saved = pocketItemService.save(item, req.pocketName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody PocketItemUpdateRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> fields = new HashMap<>();
        if (req.summary() != null) fields.put("summary", req.summary());
        if (req.content() != null) fields.put("content", req.content());
        List<EntityConfigValidator.FieldError> errors = validator.validate("PocketItem", fields);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));

        Optional<PocketItem> match = findOwnedItem(id);
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        PocketItem item = match.get();
        if (req.summary() != null) item.setSummary(req.summary());
        if (req.content() != null) item.setContent(req.content());
        if (req.encrypted() != null) item.setEncrypted(req.encrypted());
        if (req.orderInPocket() != null) item.setOrderInPocket(req.orderInPocket());
        item.setModificationDate(LocalDateTime.now());
        PocketItem saved = pocketItemService.save(item);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PocketItem> match = findOwnedItem(id);
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        pocketItemService.delete(match.get());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/decrypt")
    public ResponseEntity<DecryptResponse> decrypt(@PathVariable UUID id, @RequestBody DecryptRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PocketItem> match = findOwnedItem(id);
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        PocketItem item = match.get();
        if (!item.isEncrypted()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String decrypted = pocketItemService.decryptContent(item, req.password());
            return ResponseEntity.ok(new DecryptResponse(decrypted));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private Optional<PocketItem> findOwnedItem(UUID id) {
        return pocketItemRepository.findAllByDeletedFalseAndOwnersId(AuthService.getLoggedUserId())
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }

    private PocketItemDto toDto(PocketItem i) {
        return new PocketItemDto(
                i.getId(), i.getSummary(),
                i.isEncrypted() ? null : i.getContent(),
                i.isEncrypted(), i.getOrderInPocket(),
                i.getCreationDate(), i.getModificationDate()
        );
    }

    private Comparator<PocketItemDto> buildComparator(String sort, String direction) {
        Comparator<PocketItemDto> cmp = switch (sort) {
            case "summary" -> Comparator.comparing(PocketItemDto::summary, Comparator.nullsLast(Comparator.naturalOrder()));
            case "modificationDate" -> Comparator.comparing(PocketItemDto::modificationDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(PocketItemDto::orderInPocket, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return "desc".equalsIgnoreCase(direction) ? cmp.reversed() : cmp;
    }
}
