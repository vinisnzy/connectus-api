package com.vinisnzy.connectus_api.domain.core.mapper;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateUserRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateUserRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "role", source = "roleId")
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "status", constant = "OFFLINE")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isMaster", constant = "false")
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "metrics", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "role", source = "roleId")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isMaster", ignore = true)
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "metrics", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "roleId", source = "role.id")
    UserResponse toResponse(User user);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default Role mapRole(Integer roleId) {
        if (roleId == null) return null;
        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}
