package com.mindp.connectus_api.domain.core.mapper;

import com.mindp.connectus_api.domain.core.dto.request.CreateCompanyRequest;
import com.mindp.connectus_api.domain.core.dto.request.UpdateCompanyRequest;
import com.mindp.connectus_api.domain.core.dto.response.CompanyResponse;
import com.mindp.connectus_api.domain.core.entity.Company;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    Company toEntity(CreateCompanyRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    void updateEntity(UpdateCompanyRequest request, @MappingTarget Company company);

    CompanyResponse toResponse(Company company);
}
