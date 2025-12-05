package com.vinisnzy.connectus_api.infra.security.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
