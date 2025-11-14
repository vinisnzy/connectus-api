package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

public record CancelAppointmentRequest(
        String reason,
        Boolean notifyContact,
        Boolean releaseSlot
) {
}
