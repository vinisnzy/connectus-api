package com.vinisnzy.connectus_api.api.controller.automation;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateWhatsAppConnectionRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateWhatsAppConnectionRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WhatsAppConnectionResponse;
import com.vinisnzy.connectus_api.domain.automation.service.WhatsAppConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/whatsapp-connections")
@RequiredArgsConstructor
public class WhatsAppConnectionController {

    private final WhatsAppConnectionService service;

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'create')")
    public ResponseEntity<WhatsAppConnectionResponse> create(@RequestBody @Valid CreateWhatsAppConnectionRequest request) {
        WhatsAppConnectionResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'view')")
    public ResponseEntity<List<WhatsAppConnectionResponse>> getAll() {
        List<WhatsAppConnectionResponse> connections = service.getAll();
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'view')")
    public ResponseEntity<WhatsAppConnectionResponse> getById(@PathVariable UUID id) {
        WhatsAppConnectionResponse connection = service.getById(id);
        return ResponseEntity.ok(connection);
    }

    @PutMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'edit')")
    public ResponseEntity<WhatsAppConnectionResponse> update(@RequestBody @Valid UpdateWhatsAppConnectionRequest request) {
        WhatsAppConnectionResponse response = service.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send-connection")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'connect')")
    public ResponseEntity<Void> sendConnectionToUser(@PathVariable UUID id) {
        service.sendConnectionToUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/send-qrcode")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'whatsapp_connection', 'connect')")
    public ResponseEntity<Void> sendQRCodeToUser(@PathVariable UUID id) {
        service.sendQRCodeToUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/is-connected")
    public ResponseEntity<Boolean> isConnected(@PathVariable UUID id) {
        Boolean connected = service.isConnected(id);
        return ResponseEntity.ok(connected);
    }
}
