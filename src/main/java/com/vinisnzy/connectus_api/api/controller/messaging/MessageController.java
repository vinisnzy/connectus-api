package com.vinisnzy.connectus_api.api.controller.messaging;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.ReceiveMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.SendMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.MessageResponse;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection;
import com.vinisnzy.connectus_api.domain.messaging.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAll(Pageable pageable) {
        List<MessageResponse> messages = service.findAll(pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> findById(@PathVariable UUID id) {
        MessageResponse message = service.findById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<MessageResponse>> findByTicketId(
            @PathVariable UUID ticketId,
            Pageable pageable) {
        List<MessageResponse> messages = service.findByTicketId(ticketId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/external/{externalId}")
    public ResponseEntity<Optional<MessageResponse>> findByExternalId(@PathVariable String externalId) {
        Optional<MessageResponse> message = service.findByExternalId(externalId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/send")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'messages', 'send_message')")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody @Valid SendMessageRequest request) {
        MessageResponse message = service.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/receive")
    public ResponseEntity<MessageResponse> receiveMessage(@RequestBody @Valid ReceiveMessageRequest request) {
        MessageResponse message = service.receiveMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-multiple")
    public ResponseEntity<Void> markMultipleAsRead(@RequestBody List<UUID> messageIds) {
        service.markMultipleAsRead(messageIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/delivered")
    public ResponseEntity<Void> markAsDelivered(@PathVariable UUID id) {
        service.markAsDelivered(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ticket/{ticketId}/unread/count")
    public ResponseEntity<Long> countUnreadByTicketId(@PathVariable UUID ticketId) {
        long count = service.countUnreadByTicketId(ticketId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/company/unread/count")
    public ResponseEntity<Long> countUnreadByCompany() {
        long count = service.countUnreadByCompany();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/direction/{direction}")
    public ResponseEntity<List<MessageResponse>> findByDirection(
            @PathVariable MessageDirection direction,
            Pageable pageable) {
        List<MessageResponse> messages = service.findByDirection(direction, pageable);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'messages', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ticket/{ticketId}/last")
    public ResponseEntity<List<MessageResponse>> getLastMessages(
            @PathVariable UUID ticketId,
            @RequestParam(defaultValue = "10") int limit) {
        List<MessageResponse> messages = service.getLastMessages(ticketId, limit);
        return ResponseEntity.ok(messages);
    }
}
