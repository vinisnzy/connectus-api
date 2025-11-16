package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserRequest(
        @NotNull(message = "Id da empresa é obrigatório")
        UUID companyId,

        @NotNull(message = "Id do cargo é obrigatório")
        Integer roleId,

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        String phone,
        String avatar
) {
}
