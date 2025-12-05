package com.vinisnzy.connectus_api.api.controller.core;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateCompanyRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateCompanyRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.CompanyResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable UUID id) {
        CompanyResponse company = service.findById(id);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CompanyResponse> findByCnpj(@PathVariable String cnpj) {
        CompanyResponse company = service.findByCnpj(cnpj);
        return ResponseEntity.ok(company);
    }

    @PostMapping
    public ResponseEntity<Company> create(@RequestBody @Valid CreateCompanyRequest request) {
        Company company = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCompanyRequest request) {
        Company company = service.update(id, request);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'company', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'company', 'toggle_active')")
    public ResponseEntity<Company> toggleActive(
            @PathVariable UUID id,
            @RequestParam boolean isActive) {
        Company company = service.toggleActive(id, isActive);
        return ResponseEntity.ok(company);
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<Company> verify(@PathVariable UUID id) {
        Company company = service.verify(id);
        return ResponseEntity.ok(company);
    }

    @PatchMapping("/{id}/settings")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'company', 'edit_settings')")
    public ResponseEntity<Company> updateSettings(
            @PathVariable UUID id,
            @RequestBody String settingsJson) {
        Company company = service.updateSettings(id, settingsJson);
        return ResponseEntity.ok(company);
    }
}
