package com.vinisnzy.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

public record CreateContactRequest(
        @NotBlank(message = "Telefone é obrigatório")
        String phone,

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Email(message = "Email inválido")
        String email,

        List<String> tags,

        Map<String, Object> customData
) {


}
