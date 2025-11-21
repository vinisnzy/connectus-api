package com.vinisnzy.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateContactGroupRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        String description
) {
}
