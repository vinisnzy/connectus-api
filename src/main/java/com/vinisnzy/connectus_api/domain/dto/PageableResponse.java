package com.vinisnzy.connectus_api.domain.dto;

import lombok.Builder;

@Builder
public record PageableResponse<T>(
        T content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) {
}
