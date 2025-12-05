package com.vinisnzy.connectus_api.api.controller.core;

import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateUserRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import com.vinisnzy.connectus_api.domain.core.entity.enums.UserStatus;
import com.vinisnzy.connectus_api.domain.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(Pageable pageable) {
        List<UserResponse> users = service.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        UserResponse user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        UserResponse user = service.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponse> findCurrentUser() {
        UserResponse user = service.findCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse user = service.update(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<UserResponse> toggleActive(
            @PathVariable UUID id,
            @RequestParam boolean isActive) {
        UserResponse user = service.toggleActive(id, isActive);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        service.updatePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String email) {
        service.resetPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password-with-token")
    public ResponseEntity<Void> changePasswordWithToken(
            @RequestParam String token,
            @RequestParam String newPassword) {
        service.changePasswordWithToken(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable UUID id,
            @RequestParam Integer roleId) {
        UserResponse user = service.updateRole(id, roleId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam UserStatus status) {
        UserResponse user = service.updateStatus(id, status);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/last-seen")
    public ResponseEntity<Void> updateLastSeen(@PathVariable UUID id) {
        service.updateLastSeen(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserResponse>> findByStatus(
            @PathVariable UserStatus status,
            Pageable pageable) {
        List<UserResponse> users = service.findByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam String query) {
        List<UserResponse> users = service.search(query);
        return ResponseEntity.ok(users);
    }
}
