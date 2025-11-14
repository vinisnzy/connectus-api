package com.vinisnzy.connectus_api.domain.core.mapper;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.RoleResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "isSystemRole", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "isSystemRole", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateRoleRequest request, @MappingTarget Role role);

    RoleResponse toResponse(Role role);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
