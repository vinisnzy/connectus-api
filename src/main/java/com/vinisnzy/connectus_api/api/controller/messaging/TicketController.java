package com.vinisnzy.connectus_api.api.controller.messaging;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.AddTagsToTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.ResolveTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.UpdateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketResponse;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.TicketStatus;
import com.vinisnzy.connectus_api.domain.messaging.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;

    @GetMapping
    public ResponseEntity<List<TicketResponse>> findAll(Pageable pageable) {
        List<TicketResponse> tickets = service.findAll(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> findById(@PathVariable UUID id) {
        TicketResponse ticket = service.findById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/contact/{contactId}")
    public ResponseEntity<List<TicketResponse>> findByContactId(
            @PathVariable UUID contactId,
            Pageable pageable) {
        List<TicketResponse> tickets = service.findByContactId(contactId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<TicketResponse>> findByAssignedUserId(
            @PathVariable UUID userId,
            Pageable pageable) {
        List<TicketResponse> tickets = service.findByAssignedUserId(userId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> findByStatus(
            @PathVariable TicketStatus status,
            Pageable pageable) {
        List<TicketResponse> tickets = service.findByStatus(status, pageable);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'create')")
    public ResponseEntity<TicketResponse> create(@RequestBody @Valid CreateTicketRequest request) {
        TicketResponse ticket = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'edit')")
    public ResponseEntity<TicketResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTicketRequest request) {
        TicketResponse ticket = service.update(id, request);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<TicketResponse> assign(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        TicketResponse ticket = service.assign(id, userId);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/unassign")
    public ResponseEntity<TicketResponse> unassign(@PathVariable UUID id) {
        TicketResponse ticket = service.unassign(id);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'edit')")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam TicketStatus newStatus) {
        TicketResponse ticket = service.updateStatus(id, newStatus);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'resolve')")
    public ResponseEntity<TicketResponse> resolve(
            @PathVariable UUID id,
            @RequestBody @Valid ResolveTicketRequest request) {
        TicketResponse ticket = service.resolve(id, request);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'close')")
    public ResponseEntity<TicketResponse> close(@PathVariable UUID id) {
        TicketResponse ticket = service.close(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'reopen')")
    public ResponseEntity<TicketResponse> reopen(@PathVariable UUID id) {
        TicketResponse ticket = service.reopen(id);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/pending")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'set_pending')")
    public ResponseEntity<TicketResponse> setPending(
            @PathVariable UUID id,
            @RequestParam ZonedDateTime pendingUntil) {
        TicketResponse ticket = service.setPending(id, pendingUntil);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'archive')")
    public ResponseEntity<TicketResponse> archive(@PathVariable UUID id) {
        TicketResponse ticket = service.archive(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{id}/unarchive")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'unarchive')")
    public ResponseEntity<TicketResponse> unarchive(@PathVariable UUID id) {
        TicketResponse ticket = service.unarchive(id);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/tags")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'tickets', 'add_tags')")
    public ResponseEntity<TicketResponse> addTags(
            @PathVariable UUID id,
            @RequestBody @Valid AddTagsToTicketRequest request) {
        TicketResponse ticket = service.addTags(id, request);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TicketResponse>> findOverdue() {
        List<TicketResponse> tickets = service.findOverdue();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<TicketResponse>> findUnassigned(
            @RequestParam TicketStatus status,
            Pageable pageable) {
        List<TicketResponse> tickets = service.findUnassigned(status, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> countByStatus(@PathVariable TicketStatus status) {
        Long count = service.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/non-archived")
    public ResponseEntity<List<TicketResponse>> findNonArchived(Pageable pageable) {
        List<TicketResponse> tickets = service.findNonArchived(pageable);
        return ResponseEntity.ok(tickets);
    }
}
