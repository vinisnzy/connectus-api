package com.vinisnzy.connectus_api.domain.crm.dto.response;

import com.vinisnzy.connectus_api.domain.dto.PageableResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ContactListResponse(
        List<ContactResponse> content,
        PageableResponse pageable) {
}
