package com.vinisnzy.connectus_api.domain.messaging.dto.response;

import com.vinisnzy.connectus_api.domain.dto.PageableResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record TicketListResponse(
        List<TicketResponse> content,
        PageableResponse pageable) {
}
