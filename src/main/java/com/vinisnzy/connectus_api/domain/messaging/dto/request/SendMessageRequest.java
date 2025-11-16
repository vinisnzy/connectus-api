package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record SendMessageRequest(

        @NotNull(message = "ID da empresa é obrigatório")
        UUID companyId,

        @NotNull(message = "ID do ticket é obrigatório")
        UUID ticketId,

        @NotBlank(message = "Tipo de mensagem é obrigatório")
        String messageType,

        @NotNull(message = "Conteúdo da mensagem é obrigatório")
        Map<String, Object> content,

        @NotBlank(message = "Número do destinatário é obrigatório")
        String recipientNumber,

        @NotNull(message = "ID do usuário que enviou a mensagem é obrigatório")
        UUID senderUserId,

        @NotNull(message = "Data e hora de envio é obrigatório")
        LocalDateTime sentAt
) {
}
