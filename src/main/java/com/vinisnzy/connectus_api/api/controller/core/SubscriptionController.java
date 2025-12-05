package com.vinisnzy.connectus_api.api.controller.core;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.SubscriptionResponse;
import com.vinisnzy.connectus_api.domain.core.service.SubscriptionService;
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
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> findAll(Pageable pageable) {
        List<SubscriptionResponse> subscriptions = service.findAll(pageable);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'view')")
    public ResponseEntity<SubscriptionResponse> findById(@PathVariable UUID id) {
        SubscriptionResponse subscription = service.findById(id);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/current")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'view')")
    public ResponseEntity<SubscriptionResponse> findCurrentCompanySubscription() {
        SubscriptionResponse subscription = service.findCurrentCompanySubscription();
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'view')")
    public ResponseEntity<List<SubscriptionResponse>> findAllByCompanyId(@PathVariable UUID companyId) {
        List<SubscriptionResponse> subscriptions = service.findAllByCompanyId(companyId);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody @Valid CreateSubscriptionRequest request) {
        SubscriptionResponse subscription = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'edit')")
    public ResponseEntity<SubscriptionResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSubscriptionRequest request) {
        SubscriptionResponse subscription = service.update(id, request);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'cancel')")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable UUID id) {
        SubscriptionResponse subscription = service.cancel(id);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'renew')")
    public ResponseEntity<SubscriptionResponse> renew(@PathVariable UUID id) {
        SubscriptionResponse subscription = service.renew(id);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'suspend')")
    public ResponseEntity<SubscriptionResponse> suspend(@PathVariable UUID id) {
        SubscriptionResponse subscription = service.suspend(id);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'subscription', 'reactivate')")
    public ResponseEntity<SubscriptionResponse> reactivate(@PathVariable UUID id) {
        SubscriptionResponse subscription = service.reactivate(id);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/{id}/is-expired")
    public ResponseEntity<Boolean> isExpired(@PathVariable UUID id) {
        boolean expired = service.isExpired(id);
        return ResponseEntity.ok(expired);
    }

    @GetMapping("/{id}/is-in-trial")
    public ResponseEntity<Boolean> isInTrial(@PathVariable UUID id) {
        boolean inTrial = service.isInTrial(id);
        return ResponseEntity.ok(inTrial);
    }

    @GetMapping("/{id}/days-until-expiration")
    public ResponseEntity<Long> getDaysUntilExpiration(@PathVariable UUID id) {
        long days = service.getDaysUntilExpiration(id);
        return ResponseEntity.ok(days);
    }
}
