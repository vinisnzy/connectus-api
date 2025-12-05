package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import java.time.ZonedDateTime;

public record UpdateAppointmentRequest(
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        String notes
) {
}
