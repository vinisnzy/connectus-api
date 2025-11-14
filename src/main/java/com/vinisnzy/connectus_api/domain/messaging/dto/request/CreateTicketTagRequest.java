package com.vinisnzy.connectus_api.domain.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateTicketTagRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        String color,
        String description
) {
}
