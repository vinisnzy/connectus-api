package com.vinisnzy.connectus_api.domain.crm.mapper;

import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactGroupResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.crm.entity.ContactGroup;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContactGroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactGroup toEntity(CreateContactGroupRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateContactGroupRequest request, @MappingTarget ContactGroup contactGroup);

    ContactGroupResponse toResponse(ContactGroup contactGroup);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
