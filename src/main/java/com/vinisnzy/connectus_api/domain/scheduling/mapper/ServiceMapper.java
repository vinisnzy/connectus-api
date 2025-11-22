package com.vinisnzy.connectus_api.domain.scheduling.mapper;

import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.ServiceResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Service;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toEntity(CreateServiceRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateServiceRequest request, @MappingTarget Service service);

    @Mapping(target = "duration", source = "durationMinutes")
    ServiceResponse toResponse(Service service);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
