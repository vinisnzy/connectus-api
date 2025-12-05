package com.vinisnzy.connectus_api.domain.analytics.mapper;

import com.vinisnzy.connectus_api.domain.analytics.dto.request.NotificationRequest;
import com.vinisnzy.connectus_api.domain.analytics.dto.response.NotificationResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.Notification;
import com.vinisnzy.connectus_api.domain.analytics.entity.enums.NotificationType;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "relatedData", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Notification toEntity(NotificationRequest request);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "userId", source = "user.id")
    NotificationResponse toResponse(Notification notification);

    default User mapUser(java.util.UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default NotificationType mapNotificationType(String type) {
        if (type == null) return NotificationType.INFO;
        return NotificationType.valueOf(type.toUpperCase());
    }

    default String mapNotificationTypeToString(NotificationType type) {
        if (type == null) return null;
        return type.name();
    }
}
