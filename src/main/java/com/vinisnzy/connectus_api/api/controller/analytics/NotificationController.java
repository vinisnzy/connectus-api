package com.vinisnzy.connectus_api.api.controller.analytics;

import com.vinisnzy.connectus_api.domain.analytics.dto.request.NotificationRequest;
import com.vinisnzy.connectus_api.domain.analytics.dto.response.NotificationResponse;
import com.vinisnzy.connectus_api.domain.analytics.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        List<NotificationResponse> notifications = service.getNotificationsForUser(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID notificationId) {
        NotificationResponse notification = service.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable UUID userId) {
        Long count = service.countUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/user")
    public ResponseEntity<Void> sendNotificationToUser(@RequestBody @Valid NotificationRequest request) {
        service.sendNotificationToUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/company")
    public ResponseEntity<Void> sendNotificationToCompany(@RequestBody @Valid NotificationRequest request) {
        service.sendNotificationToCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
