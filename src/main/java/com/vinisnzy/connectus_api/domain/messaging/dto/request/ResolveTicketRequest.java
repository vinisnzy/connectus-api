package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import com.vinisnzy.connectus_api.domain.messaging.entity.enums.ResolutionType;

public record ResolveTicketRequest(
        ResolutionType resolutionType,
        String resolutionNotes
) {
}
