package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record RescheduleAppointmentRequest(
        @NotNull(message = "Nova data e hora de início são obrigatórias")
        @Future(message = "Nova data e hora de início devem ser no futuro")
        ZonedDateTime newStartTime,

        @NotNull(message = "Nova data e hora de término são obrigatórias")
        @Future(message = "Nova data e hora de término devem ser no futuro")
        ZonedDateTime newEndTime,

        Boolean sendNotification
) {
}
