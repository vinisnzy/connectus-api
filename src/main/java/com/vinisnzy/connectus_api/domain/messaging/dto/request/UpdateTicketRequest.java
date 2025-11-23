package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import java.util.List;

public record UpdateTicketRequest(
        Integer priority,
        String category,
        List<Integer> tagIds
) {
}
