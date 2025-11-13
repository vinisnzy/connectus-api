package com.mindp.connectus_api.domain.messaging.dto.request;

import lombok.Builder;

@Builder
public record UpdateTicketTagRequest(
        String name,
        String color,
        String description
) {
}
