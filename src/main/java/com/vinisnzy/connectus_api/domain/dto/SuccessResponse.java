package com.vinisnzy.connectus_api.domain.dto;

import lombok.Builder;

@Builder
public record SuccessResponse(
        Boolean success,
        String message) {
}
