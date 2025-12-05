package com.vinisnzy.connectus_api.api.controller.crm;

import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactGroupResponse;
import com.vinisnzy.connectus_api.domain.crm.service.ContactGroupService;
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
@RequestMapping("/api/contact-groups")
@RequiredArgsConstructor
public class ContactGroupController {

    private final ContactGroupService service;

    @GetMapping
    public ResponseEntity<List<ContactGroupResponse>> findAll(Pageable pageable) {
        List<ContactGroupResponse> groups = service.findAll(pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactGroupResponse> findById(@PathVariable UUID id) {
        ContactGroupResponse group = service.findById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ContactGroupResponse> findByName(@PathVariable String name) {
        ContactGroupResponse group = service.findByName(name);
        return ResponseEntity.ok(group);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contact_groups', 'create')")
    public ResponseEntity<ContactGroupResponse> create(@RequestBody @Valid CreateContactGroupRequest request) {
        ContactGroupResponse group = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contact_groups', 'edit')")
    public ResponseEntity<ContactGroupResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateContactGroupRequest request) {
        ContactGroupResponse group = service.update(id, request);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contact_groups', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/count")
    public ResponseEntity<Long> countContacts(@PathVariable UUID id) {
        long count = service.countContacts(id);
        return ResponseEntity.ok(count);
    }
}
