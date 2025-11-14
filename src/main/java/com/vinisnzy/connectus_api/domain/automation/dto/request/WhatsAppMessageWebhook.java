package com.vinisnzy.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record WhatsAppMessageWebhook(
        @NotNull(message = "ID da conexão é obrigatório")
        UUID connectionId,

        @NotBlank(message = "Número de origem é obrigatório")
        String from,

        @NotBlank(message = "Tipo de mensagem é obrigatório")
        String messageType,

        @NotNull(message = "Conteúdo da mensagem é obrigatório")
        Map<String, Object> content,

        @NotNull(message = "Timestamp é obrigatório")
        LocalDateTime timestamp
) {
}
