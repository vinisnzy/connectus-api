package com.vinisnzy.connectus_api.infra.security.dto;

import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String email,
        String name
) {
}
