package com.vinisnzy.connectus_api.api.controller.core;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateRolePermissionsRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.RoleResponse;
import com.vinisnzy.connectus_api.domain.core.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> findAll() {
        List<RoleResponse> roles = service.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Integer id) {
        RoleResponse role = service.findById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/system")
    public ResponseEntity<List<RoleResponse>> findSystemRoles() {
        List<RoleResponse> roles = service.findSystemRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'roles', 'create')")
    public ResponseEntity<RoleResponse> create(@RequestBody @Valid CreateRoleRequest request) {
        RoleResponse role = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'roles', 'edit')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateRoleRequest request) {
        RoleResponse role = service.update(id, request);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'roles', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/permissions")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'roles', 'edit')")
    public ResponseEntity<RoleResponse> updatePermissions(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateRolePermissionsRequest request) {
        RoleResponse role = service.updatePermissions(id, request);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{id}/has-permission")
    public ResponseEntity<Boolean> hasPermission(
            @PathVariable Integer id,
            @RequestParam String resource,
            @RequestParam String action) {
        boolean hasPermission = service.hasPermission(id, resource, action);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping("/{id}/clone")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'roles', 'create')")
    public ResponseEntity<RoleResponse> clone(
            @PathVariable Integer id,
            @RequestParam String newName) {
        RoleResponse role = service.clone(id, newName);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
}
