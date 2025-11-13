package com.mindp.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateWhatsAppConnectionRequest(

        @NotNull(message = "Id da conexão é obrigatório")
        UUID id,

        @NotBlank(message = "Nome da conexão é obrigatório")
        @Size(max = 100, message = "Nome da conexão deve ter no máximo 100 caracteres")
        String connectionName
) {
}
