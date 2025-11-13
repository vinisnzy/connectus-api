package com.mindp.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTicketRequest(
    @NotNull(message = "ID do usuário é obrigatório")
    UUID userId
) {
}
