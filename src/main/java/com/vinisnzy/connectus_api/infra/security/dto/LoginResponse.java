package com.vinisnzy.connectus_api.infra.security.dto;

import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String refreshToken,
        String userId,
        Long expiresIn) {
}
