package com.mindp.connectus_api.domain.analytics.dto.request;

import com.mindp.connectus_api.domain.analytics.entity.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record NotificationRequest (
        UUID userId,
        UUID companyId,

        @NotBlank(message = "Título obrigatório")
        String title,

        @NotBlank(message = "Mensagem obrigatório")
        String message,

        NotificationType type
) {
}
