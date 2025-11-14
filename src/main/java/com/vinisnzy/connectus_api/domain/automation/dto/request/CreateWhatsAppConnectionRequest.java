package com.vinisnzy.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWhatsAppConnectionRequest(
        @NotBlank(message = "Nome da conexão é obrigatório")
        @Size(max = 100, message = "Nome da conexão deve ter no máximo 100 caracteres")
        String connectionName
) {
}
