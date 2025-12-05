package com.vinisnzy.connectus_api.domain.analytics.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.dto.request.NotificationRequest;
import com.vinisnzy.connectus_api.domain.analytics.dto.response.NotificationResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.Notification;
import com.vinisnzy.connectus_api.domain.analytics.mapper.NotificationMapper;
import com.vinisnzy.connectus_api.domain.analytics.repository.NotificationRepository;
import com.vinisnzy.connectus_api.infra.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final ActivityLogService activityLogService;

    private final WebSocketService webSocketService;

    private final NotificationMapper mapper;

    public List<NotificationResponse> getNotificationsForUser(UUID userId, Pageable pageable) {
        Page<Notification> notifications = repository.findByUserIdOrderByCreatedAt(userId, pageable);
        return notifications.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public NotificationResponse markNotificationAsRead(UUID notificationId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada com o id: " + notificationId));

        notification.setIsRead(true);
        repository.save(notification);

        return mapper.toResponse(notification);
    }

    public Long countUnreadNotificationsForUser(UUID userId) {
        return repository.countByUserIdAndIsReadFalse(userId);
    }

    public void sendNotificationToUser(NotificationRequest request) {
        Notification notification = mapper.toEntity(request);
        notification = repository.save(notification);

        activityLogService.log("ENTITY_CREATED", "Notification", notification.getId());

        NotificationResponse payload = mapper.toResponse(notification);

        webSocketService.sendToUser(request.userId(), "/queue/notifications", payload);
    }

    public void sendNotificationToCompany(NotificationRequest request) {
        Notification notification = mapper.toEntity(request);
        notification = repository.save(notification);

        activityLogService.log("ENTITY_CREATED", "Notification", notification.getId());

        NotificationResponse response = mapper.toResponse(notification);

        String topic = "/topic/company/" + notification.getUser().getCompany().getId() + "/notifications";
        webSocketService.sendToTopic(topic, response);
    }
}
