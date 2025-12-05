package com.vinisnzy.connectus_api.api.controller.crm;

import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactResponse;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.service.ContactService;
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
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;

    @GetMapping
    public ResponseEntity<List<ContactResponse>> findAll(Pageable pageable) {
        List<ContactResponse> contacts = service.findAll(pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> findById(@PathVariable UUID id) {
        ContactResponse contact = service.findById(id);
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ContactResponse> findByPhone(@PathVariable String phone) {
        ContactResponse contact = service.findByPhone(phone);
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ContactResponse> findByEmail(@PathVariable String email) {
        ContactResponse contact = service.findByEmail(email);
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'create')")
    public ResponseEntity<ContactResponse> create(@RequestBody @Valid CreateContactRequest request) {
        ContactResponse contact = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'edit')")
    public ResponseEntity<ContactResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateContactRequest request) {
        ContactResponse contact = service.update(id, request);
        return ResponseEntity.ok(contact);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-block")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'toggle_block')")
    public ResponseEntity<ContactResponse> toggleBlock(
            @PathVariable UUID id,
            @RequestParam boolean isBlocked) {
        ContactResponse contact = service.toggleBlock(id, isBlocked);
        return ResponseEntity.ok(contact);
    }

    @PatchMapping("/{id}/last-interaction")
    public ResponseEntity<Void> updateLastInteraction(@PathVariable UUID id) {
        service.updateLastInteraction(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/tags/add")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'add_tags')")
    public ResponseEntity<ContactResponse> addTags(
            @PathVariable UUID id,
            @RequestBody String[] tags) {
        ContactResponse contact = service.addTags(id, tags);
        return ResponseEntity.ok(contact);
    }

    @PatchMapping("/{id}/tags/remove")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'remove_tags')")
    public ResponseEntity<ContactResponse> removeTags(
            @PathVariable UUID id,
            @RequestBody String[] tags) {
        ContactResponse contact = service.removeTags(id, tags);
        return ResponseEntity.ok(contact);
    }

    @PatchMapping("/{id}/groups/add")
    public ResponseEntity<ContactResponse> addToGroups(
            @PathVariable UUID id,
            @RequestBody UUID[] groupIds) {
        ContactResponse contact = service.addToGroups(id, groupIds);
        return ResponseEntity.ok(contact);
    }

    @PatchMapping("/{id}/groups/remove")
    public ResponseEntity<ContactResponse> removeFromGroups(
            @PathVariable UUID id,
            @RequestBody UUID[] groupIds) {
        ContactResponse contact = service.removeFromGroups(id, groupIds);
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<ContactResponse>> findByTag(
            @PathVariable String tag,
            Pageable pageable) {
        List<ContactResponse> contacts = service.findByTag(tag, pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ContactResponse>> findByGroup(
            @PathVariable UUID groupId,
            Pageable pageable) {
        List<ContactResponse> contacts = service.findByGroup(groupId, pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactResponse>> search(
            @RequestParam String query,
            Pageable pageable) {
        List<ContactResponse> contacts = service.search(query, pageable);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping("/import")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'contacts', 'import')")
    public ResponseEntity<List<ContactResponse>> importContacts(@RequestBody List<Contact> contacts) {
        List<ContactResponse> importedContacts = service.importContacts(contacts);
        return ResponseEntity.status(HttpStatus.CREATED).body(importedContacts);
    }
}
