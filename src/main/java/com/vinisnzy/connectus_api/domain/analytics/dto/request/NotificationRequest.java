package com.vinisnzy.connectus_api.domain.analytics.dto.request;

import com.vinisnzy.connectus_api.domain.analytics.entity.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationRequest (
        UUID userId,

        @NotBlank(message = "Título é obrigatório")
        String title,

        @NotBlank(message = "Mensagem é obrigatório")
        String message,

        NotificationType type
) {
}
