package com.mindp.connectus_api.infra.security.dto;

import com.mindp.connectus_api.domain.core.dto.response.UserResponse;
import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String refreshToken,
        UserResponse user,
        Long expiresIn) {
}
