package com.vinisnzy.connectus_api.domain.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PeriodResponse(
        LocalDate startDate,
        LocalDate endDate) {
}
