package com.mindp.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.NotNull;

public record BlockContactRequest(
        @NotNull(message = "Status de bloqueio é obrigatório")
        Boolean blocked,
        String reason
) {
}
