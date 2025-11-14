package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PendingTicketRequest(
        @NotNull(message = "Data e hora de pendência são obrigatórias")
        @Future(message = "Data e hora devem ser no futuro")
        LocalDateTime pendingUntil,

        String reason
) {
}
