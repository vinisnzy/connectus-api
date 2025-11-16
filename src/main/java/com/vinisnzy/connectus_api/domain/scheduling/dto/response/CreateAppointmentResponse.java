package com.vinisnzy.connectus_api.domain.scheduling.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateAppointmentResponse(
        UUID id,
        String status,
        Boolean confirmationSent
) {
}
