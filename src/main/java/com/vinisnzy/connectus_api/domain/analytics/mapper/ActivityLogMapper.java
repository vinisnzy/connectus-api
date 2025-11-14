package com.vinisnzy.connectus_api.domain.analytics.mapper;

import com.vinisnzy.connectus_api.domain.analytics.dto.response.ActivityLogResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.ActivityLog;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    @Mapping(target = "changes", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    ActivityLogResponse toResponse(ActivityLog activityLog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    ActivityLog toEntity(String action, String entityType, UUID entityId, UUID companyId, UUID userId);

    default Company mapCompany(UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default User mapUser(UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
