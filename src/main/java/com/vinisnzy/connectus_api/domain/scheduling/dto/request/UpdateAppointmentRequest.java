package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UpdateAppointmentRequest(
        @NotNull(message = "Id do agendamento é obrigatório")
        UUID id,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        String notes
) {
}
