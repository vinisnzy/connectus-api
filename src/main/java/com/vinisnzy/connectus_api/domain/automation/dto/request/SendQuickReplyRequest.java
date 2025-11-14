package com.vinisnzy.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendQuickReplyRequest(
        @NotNull(message = "ID da resposta rápida é obrigatório")
        UUID quickReplyId
) {
}
