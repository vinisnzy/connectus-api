package com.vinisnzy.connectus_api.infra.security.dto.response;

import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String email,
        String name
) {
}
