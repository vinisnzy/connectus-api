package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ReceiveMessageRequest(
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
