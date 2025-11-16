package com.vinisnzy.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateQuickReplyRequest(
        @NotBlank(message = "Atalho é obrigatório")
        @Size(max = 50, message = "Atalho deve ter no máximo 50 caracteres")
        String shortcut,

        @NotBlank(message = "Título é obrigatório")
        @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
        String title,

        @NotBlank(message = "Conteúdo da mensagem é obrigatório")
        String messageContent,

        String mediaUrl
) {
}
