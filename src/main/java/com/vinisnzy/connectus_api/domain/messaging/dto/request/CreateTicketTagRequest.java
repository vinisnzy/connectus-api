package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record CreateTicketTagRequest(
        @NotBlank(message = "Nome da tag é obrigatório")
        String name,

        @NotBlank(message = "Cor da tag é obrigatória")
        String color,

        String description
) {
}
