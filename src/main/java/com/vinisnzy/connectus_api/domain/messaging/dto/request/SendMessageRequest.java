package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendMessageRequest(
        @NotBlank(message = "Tipo de mensagem é obrigatório")
        String messageType,

        @NotNull(message = "Conteúdo da mensagem é obrigatório")
        Map<String, Object> content
) {
}
