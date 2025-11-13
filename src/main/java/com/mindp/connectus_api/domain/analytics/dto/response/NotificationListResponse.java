package com.mindp.connectus_api.domain.analytics.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record NotificationListResponse(
        List<NotificationResponse> content,
        Long unreadCount) {
}
