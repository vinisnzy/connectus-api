package com.vinisnzy.connectus_api.api.controller.core;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdatePricingPlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.PlanResponse;
import com.vinisnzy.connectus_api.domain.core.service.PlanService;
import com.vinisnzy.connectus_api.shared.dto.UpdateJsonRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService service;

    @GetMapping
    public ResponseEntity<List<PlanResponse>> findAll() {
        List<PlanResponse> plans = service.findAll();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PlanResponse>> findAllActive() {
        List<PlanResponse> plans = service.findAllActive();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> findById(@PathVariable UUID id) {
        PlanResponse plan = service.findById(id);
        return ResponseEntity.ok(plan);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'create')")
    public ResponseEntity<PlanResponse> create(@RequestBody @Valid CreatePlanRequest request) {
        PlanResponse plan = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'edit')")
    public ResponseEntity<PlanResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePlanRequest request) {
        PlanResponse plan = service.update(id, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'toggle_active')")
    public ResponseEntity<PlanResponse> toggleActive(
            @PathVariable UUID id,
            @RequestParam boolean isActive) {
        PlanResponse plan = service.toggleActive(id, isActive);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{id}/pricing")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'edit')")
    public ResponseEntity<PlanResponse> updatePricing(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePricingPlanRequest request) {
        PlanResponse plan = service.updatePricing(id, request);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{id}/limits")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'edit')")
    public ResponseEntity<PlanResponse> updateLimits(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateJsonRequest request) {
        PlanResponse plan = service.updateLimits(id, request);
        return ResponseEntity.ok(plan);
    }

    @PatchMapping("/{id}/features")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'plans', 'edit')")
    public ResponseEntity<PlanResponse> updateFeatures(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateJsonRequest request) {
        PlanResponse plan = service.updateFeatures(id, request);
        return ResponseEntity.ok(plan);
    }
}
