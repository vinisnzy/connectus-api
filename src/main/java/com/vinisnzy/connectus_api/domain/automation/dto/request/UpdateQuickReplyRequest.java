package com.vinisnzy.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateQuickReplyRequest(

        @NotNull(message = "Id é obrigatório")
        UUID id,

        @Size(max = 50, message = "Atalho deve ter no máximo 50 caracteres")
        String shortcut,

        @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
        String title,

        String message,

        String mediaUrl
) {
}
