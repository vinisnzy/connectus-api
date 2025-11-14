package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull(message = "Status é obrigatório")
        String status,

        String resolutionType,

        Boolean sendFarewellMessage,

        String notes
) {
}
