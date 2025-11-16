package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateTicketTagRequest(
        @NotNull(message = "ID da empresa é obrigatório")
        UUID companyId,

        @NotBlank(message = "Nome da tag é obrigatório")
        String name,

        @NotBlank(message = "Cor da tag é obrigatória")
        String color,

        String description
) {
}
