package com.mindp.connectus_api.domain.scheduling.dto.response;

import com.mindp.connectus_api.domain.dto.PageableResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record AppointmentListResponse(
        List<AppointmentResponse> content,
        PageableResponse pageable) {
}
