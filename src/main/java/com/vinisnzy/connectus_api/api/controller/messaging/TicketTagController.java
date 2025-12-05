package com.vinisnzy.connectus_api.api.controller.messaging;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketTagRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.UpdateTicketTagRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketTagResponse;
import com.vinisnzy.connectus_api.domain.messaging.service.TicketTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-tags")
@RequiredArgsConstructor
public class TicketTagController {

    private final TicketTagService service;

    @GetMapping
    public ResponseEntity<List<TicketTagResponse>> findAll(Pageable pageable) {
        List<TicketTagResponse> tags = service.findAll(pageable);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketTagResponse> findById(@PathVariable Integer id) {
        TicketTagResponse tag = service.findById(id);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TicketTagResponse> findByName(@PathVariable String name) {
        TicketTagResponse tag = service.findByName(name);
        return ResponseEntity.ok(tag);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ticket_tags', 'create')")
    public ResponseEntity<TicketTagResponse> create(@RequestBody @Valid CreateTicketTagRequest request) {
        TicketTagResponse tag = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ticket_tags', 'edit')")
    public ResponseEntity<TicketTagResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateTicketTagRequest request) {
        TicketTagResponse tag = service.update(id, request);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'ticket_tags', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
