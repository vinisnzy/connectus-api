package com.mindp.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserRequest(
        @NotNull UUID companyId,
        @NotNull Integer roleId,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        String phone,
        String avatar
) {
}
