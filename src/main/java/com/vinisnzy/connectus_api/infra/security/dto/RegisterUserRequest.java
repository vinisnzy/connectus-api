package com.vinisnzy.connectus_api.infra.security.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RegisterUserRequest(

        @NotNull(message = "ID da empresa é obrigatório.")
        UUID companyId,

        @NotNull(message = "ID da função é obrigatório.")
        Integer roleId,

        @NotBlank(message = "Nome é obrigatório.")
        @Size(min = 3, max = 80, message = "Nome deve ter entre 3 e 80 caracteres.")
        String name,

        @NotBlank(message = "Email é obrigatório.")
        @Email(message = "Email informado é inválido.")
        String email,

        @NotBlank(message = "Telefone é obrigatório.")
        @Pattern(
                regexp = "^\\+?\\d{10,15}$",
                message = "O telefone deve conter entre 10 e 15 dígitos, podendo incluir o prefixo internacional."
        )
        String phone,

        @NotBlank(message = "Senha é obrigatória.")
        @Size(min = 6, message = "A senha deve conter pelo menos 6 caracteres.")
        String password,

        @NotNull(message = "Necessário informar se o usuário é master.")
        Boolean isMaster
) {
}

