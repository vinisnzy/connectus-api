package com.vinisnzy.connectus_api.api.controller.scheduling;

import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.RescheduleAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.AppointmentResponse;
import com.vinisnzy.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import com.vinisnzy.connectus_api.domain.scheduling.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll(Pageable pageable) {
        List<AppointmentResponse> appointments = service.findAll(pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable UUID id) {
        AppointmentResponse appointment = service.findById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/contact/{contactId}")
    public ResponseEntity<List<AppointmentResponse>> findByContactId(
            @PathVariable UUID contactId,
            Pageable pageable) {
        List<AppointmentResponse> appointments = service.findByContactId(contactId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<AppointmentResponse>> findByAssignedUserId(
            @PathVariable UUID userId,
            Pageable pageable) {
        List<AppointmentResponse> appointments = service.findByAssignedUserId(userId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<AppointmentResponse>> findByServiceId(
            @PathVariable UUID serviceId,
            Pageable pageable) {
        List<AppointmentResponse> appointments = service.findByServiceId(serviceId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponse>> findByStatus(
            @PathVariable AppointmentStatus status,
            Pageable pageable) {
        List<AppointmentResponse> appointments = service.findByStatus(status, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentResponse>> findByDateRange(
            @RequestParam ZonedDateTime startDate,
            @RequestParam ZonedDateTime endDate) {
        List<AppointmentResponse> appointments = service.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'create')")
    public ResponseEntity<AppointmentResponse> create(@RequestBody @Valid CreateAppointmentRequest request) {
        AppointmentResponse appointment = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'edit')")
    public ResponseEntity<AppointmentResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateAppointmentRequest request
    ) {
        AppointmentResponse appointment = service.update(id, request);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<AppointmentResponse> assign(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        AppointmentResponse appointment = service.assign(id, userId);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'edit')")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus newStatus) {
        AppointmentResponse appointment = service.updateStatus(id, newStatus);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'confirm')")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable UUID id) {
        AppointmentResponse appointment = service.confirm(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'complete')")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable UUID id) {
        AppointmentResponse appointment = service.complete(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'cancel')")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable UUID id,
            @RequestParam String reason) {
        AppointmentResponse appointment = service.cancel(id, reason);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/mark-no-show")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'mark_no_show')")
    public ResponseEntity<AppointmentResponse> markNoShow(@PathVariable UUID id) {
        AppointmentResponse appointment = service.markNoShow(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/reschedule")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'reschedule')")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable UUID id,
            @RequestBody @Valid RescheduleAppointmentRequest request) {
        AppointmentResponse appointment = service.reschedule(id, request);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/send-reminder")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'appointments', 'send_reminder')")
    public ResponseEntity<Void> sendReminder(@PathVariable UUID id) {
        service.sendReminder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> isTimeSlotAvailable(
            @RequestParam UUID userId,
            @RequestParam ZonedDateTime startTime,
            @RequestParam ZonedDateTime endTime,
            @RequestParam(required = false) UUID excludeAppointmentId) {
        boolean available = service.isTimeSlotAvailable(userId, startTime, endTime, excludeAppointmentId);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponse>> findUpcoming(
            @RequestParam(defaultValue = "10") int limit) {
        List<AppointmentResponse> appointments = service.findUpcoming(limit);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> countByStatus(@PathVariable AppointmentStatus status) {
        Long count = service.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/contact/{contactId}/last")
    public ResponseEntity<List<AppointmentResponse>> getLastAppointments(
            @PathVariable UUID contactId,
            @RequestParam(defaultValue = "5") int limit) {
        List<AppointmentResponse> appointments = service.getLastAppointments(contactId, limit);
        return ResponseEntity.ok(appointments);
    }
}
