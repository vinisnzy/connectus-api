package com.mindp.connectus_api.domain.messaging.dto.response;

import com.mindp.connectus_api.domain.dto.PageableResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record MessageListResponse(
        List<MessageResponse> content,
        PageableResponse pageable) {
}
