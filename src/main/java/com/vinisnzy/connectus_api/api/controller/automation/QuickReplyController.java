package com.vinisnzy.connectus_api.api.controller.automation;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.QuickReplyResponse;
import com.vinisnzy.connectus_api.domain.automation.service.QuickReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quick-replies")
@RequiredArgsConstructor
public class QuickReplyController {

    private final QuickReplyService service;

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'create')")
    public ResponseEntity<QuickReplyResponse> create(@RequestBody @Valid CreateQuickReplyRequest request) {
        QuickReplyResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'view')")
    public ResponseEntity<List<QuickReplyResponse>> getAll(Pageable pageable) {
        List<QuickReplyResponse> quickReplies = service.getAll(pageable);
        return ResponseEntity.ok(quickReplies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'view')")
    public ResponseEntity<QuickReplyResponse> getById(@PathVariable UUID id) {
        QuickReplyResponse quickReply = service.getById(id);
        return ResponseEntity.ok(quickReply);
    }

    @GetMapping("/search")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'view')")
    public ResponseEntity<List<QuickReplyResponse>> getByName(@RequestParam String title) {
        List<QuickReplyResponse> quickReplies = service.getByName(title);
        return ResponseEntity.ok(quickReplies);
    }

    @PutMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'edit')")
    public ResponseEntity<QuickReplyResponse> update(@RequestBody @Valid UpdateQuickReplyRequest request) {
        QuickReplyResponse response = service.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'quick_replies', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
