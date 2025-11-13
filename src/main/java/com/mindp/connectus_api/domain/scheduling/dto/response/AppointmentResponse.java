package com.mindp.connectus_api.domain.scheduling.dto.response;

import com.mindp.connectus_api.domain.core.dto.response.UserResponse;
import com.mindp.connectus_api.domain.crm.dto.response.ContactResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AppointmentResponse(
        UUID id,
        ContactResponse contact,
        ServiceResponse service,
        UserResponse assignedUser,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String notes,
        String cancellationReason,
        LocalDateTime reminderSentAt,
        LocalDateTime createdAt) {
}
