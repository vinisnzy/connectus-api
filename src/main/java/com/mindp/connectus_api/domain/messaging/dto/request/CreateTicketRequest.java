package com.mindp.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketRequest(
        @NotNull(message = "ID do contato é obrigatório")
        UUID contactId,

        UUID assignedUserId,

        String initialMessage
) {
}
