package com.mindp.connectus_api.domain.dto;

import lombok.Builder;

@Builder
public record SuccessResponse(
        Boolean success,
        String message) {
}
