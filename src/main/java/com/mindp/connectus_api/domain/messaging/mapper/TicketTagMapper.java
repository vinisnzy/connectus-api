package com.mindp.connectus_api.domain.messaging.mapper;

import com.mindp.connectus_api.domain.messaging.dto.request.CreateTicketTagRequest;
import com.mindp.connectus_api.domain.messaging.dto.request.UpdateTicketTagRequest;
import com.mindp.connectus_api.domain.messaging.dto.response.TicketTagResponse;
import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.messaging.entity.TicketTag;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketTagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "createdAt", ignore = true)
    TicketTag toEntity(CreateTicketTagRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateTicketTagRequest request, @MappingTarget TicketTag ticketTag);

    TicketTagResponse toResponse(TicketTag ticketTag);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
