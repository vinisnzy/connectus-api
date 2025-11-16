package com.vinisnzy.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.Email;

import java.util.List;
import java.util.Map;

public record UpdateContactRequest(
        String name,

        @Email(message = "Email inválido")
        String email,

        List<String> tags,

        Map<String, Object> customData
) {
}
