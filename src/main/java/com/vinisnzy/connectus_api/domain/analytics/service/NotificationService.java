package com.vinisnzy.connectus_api.domain.analytics.service;

import com.vinisnzy.connectus_api.domain.analytics.dto.request.NotificationRequest;
import com.vinisnzy.connectus_api.domain.analytics.dto.response.NotificationResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.Notification;
import com.vinisnzy.connectus_api.domain.analytics.mapper.NotificationMapper;
import com.vinisnzy.connectus_api.domain.analytics.repository.NotificationRepository;
import com.vinisnzy.connectus_api.infra.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final WebSocketService webSocketService;

    private final NotificationMapper mapper;

    public void sendNotificationToUser(NotificationRequest request) {
        Notification notification = mapper.toEntity(request);
        repository.save(notification);
        NotificationResponse payload = mapper.toResponse(notification);

        webSocketService.sendToUser(request.userId(), "/queue/notifications", payload);
    }

    public void sendNotificationToCompany(NotificationRequest request) {
        Notification notification = mapper.toEntity(request);
        repository.save(notification);
        NotificationResponse response = mapper.toResponse(notification);

        String topic = "/topic/company/" + notification.getUser().getCompany().getId() + "/notifications";
        webSocketService.sendToTopic(topic, response);
    }
}
