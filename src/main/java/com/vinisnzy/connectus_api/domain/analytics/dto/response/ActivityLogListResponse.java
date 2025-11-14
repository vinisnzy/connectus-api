package com.vinisnzy.connectus_api.domain.analytics.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ActivityLogListResponse(
        List<ActivityLogResponse> content) {
}
