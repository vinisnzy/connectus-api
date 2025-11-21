package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
        String name,

        @Email(message = "Email inválido")
        String email,

        @Pattern(
                regexp = "^\\+?\\d{10,15}$",
                message = "O telefone deve conter entre 10 e 15 dígitos, podendo incluir o prefixo internacional."
        )
        String phone,
        String avatar,
        Integer roleId,
        Boolean isActive
) {
}
