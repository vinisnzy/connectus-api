package com.vinisnzy.connectus_api.infra.security.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
