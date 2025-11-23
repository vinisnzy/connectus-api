package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import java.util.List;

public record AddTagsToTicketRequest(
        List<Integer> tagIds
) {
}
