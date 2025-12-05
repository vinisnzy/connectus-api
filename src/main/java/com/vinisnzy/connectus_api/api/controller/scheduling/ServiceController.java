package com.vinisnzy.connectus_api.api.controller.scheduling;

import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.ServiceResponse;
import com.vinisnzy.connectus_api.domain.scheduling.service.SchedulingService;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final SchedulingService service;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> findAll(Pageable pageable) {
        List<ServiceResponse> services = service.findAll(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceResponse>> findAllActive(Pageable pageable) {
        List<ServiceResponse> services = service.findAllActive(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findById(@PathVariable UUID id) {
        ServiceResponse serviceResponse = service.findById(id);
        return ResponseEntity.ok(serviceResponse);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'services', 'create')")
    public ResponseEntity<ServiceResponse> create(@RequestBody @Valid CreateServiceRequest request) {
        ServiceResponse serviceResponse = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'services', 'edit')")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateServiceRequest request) {
        ServiceResponse serviceResponse = service.update(id, request);
        return ResponseEntity.ok(serviceResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'services', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'services', 'toggle_active')")
    public ResponseEntity<ServiceResponse> toggleActive(
            @PathVariable UUID id,
            @RequestParam boolean isActive) {
        ServiceResponse serviceResponse = service.toggleActive(id, isActive);
        return ResponseEntity.ok(serviceResponse);
    }

    @PostMapping("/{id}/clone")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'services', 'create')")
    public ResponseEntity<ServiceResponse> clone(
            @PathVariable UUID id,
            @RequestParam String newName) {
        ServiceResponse serviceResponse = service.clone(id, newName);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponse>> search(
            @RequestParam String query,
            Pageable pageable) {
        List<ServiceResponse> services = service.search(query, pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active-with-price")
    public ResponseEntity<List<ServiceResponse>> findActiveWithPrice() {
        List<ServiceResponse> services = service.findActiveWithPrice();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActive() {
        Long count = service.countActive();
        return ResponseEntity.ok(count);
    }
}
