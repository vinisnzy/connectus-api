package com.vinisnzy.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuickReplyListResponse(
        List<QuickReplyResponse> content) {
}
