package com.mindp.connectus_api.domain.messaging.dto.response;

import lombok.Builder;

@Builder
public record TicketTagResponse(
        Integer id,
        String name,
        String color,
        String description
) {
}
