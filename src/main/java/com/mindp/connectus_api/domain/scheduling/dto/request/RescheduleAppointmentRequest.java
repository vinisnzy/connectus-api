package com.mindp.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentRequest(
        @NotNull(message = "Nova data e hora de início são obrigatórias")
        @Future(message = "Nova data e hora de início devem ser no futuro")
        LocalDateTime newStartTime,

        @NotNull(message = "Nova data e hora de término são obrigatórias")
        @Future(message = "Nova data e hora de término devem ser no futuro")
        LocalDateTime newEndTime,

        Boolean sendNotification
) {
}
