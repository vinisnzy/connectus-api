package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateCompanyRequest(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj
) {
}
