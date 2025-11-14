package com.vinisnzy.connectus_api.domain.automation.mapper;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateWebhookRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateWebhookRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WebhookResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.Webhook;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Webhook toEntity(CreateWebhookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateWebhookRequest request, @MappingTarget Webhook webhook);

    WebhookResponse toResponse(Webhook webhook);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }
}
