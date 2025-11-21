package com.vinisnzy.connectus_api.domain.core.dto.request;

import java.util.Map;

public record UpdateRolePermissionsRequest(Map<String, Map<String, Boolean>> permissions) {
}
