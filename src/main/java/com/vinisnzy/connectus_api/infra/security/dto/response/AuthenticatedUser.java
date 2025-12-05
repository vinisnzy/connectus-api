package com.vinisnzy.connectus_api.infra.security.dto.response;

import java.security.Principal;
import java.util.UUID;

public record AuthenticatedUser(UUID id, UUID companyId, Integer roleId) implements Principal {
    @Override
    public String getName() {
        return id.toString();
    }
}
