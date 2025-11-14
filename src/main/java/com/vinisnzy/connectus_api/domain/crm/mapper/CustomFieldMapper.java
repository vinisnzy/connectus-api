package com.vinisnzy.connectus_api.domain.crm.mapper;

import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateCustomFieldRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateCustomFieldRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.CustomFieldResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.crm.entity.CustomField;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomFieldMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "createdAt", ignore = true)
    CustomField toEntity(CreateCustomFieldRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateCustomFieldRequest request, @MappingTarget CustomField customField);

    CustomFieldResponse toResponse(CustomField customField);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
