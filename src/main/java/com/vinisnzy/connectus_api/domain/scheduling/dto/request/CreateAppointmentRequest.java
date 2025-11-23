package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull(message = "ID do contato é obrigatório")
        UUID contactId,

        @NotNull(message = "ID do serviço é obrigatório")
        UUID serviceId,

        @NotNull(message = "ID do usuário responsável é obrigatório")
        UUID assignedUserId,

        @NotNull(message = "Data e hora de início são obrigatórias")
        @Future(message = "Data e hora de início devem ser no futuro")
        ZonedDateTime startTime,

        @NotNull(message = "Data e hora de término são obrigatórias")
        @Future(message = "Data e hora de término devem ser no futuro")
        ZonedDateTime endTime,

        String notes
) {
}
