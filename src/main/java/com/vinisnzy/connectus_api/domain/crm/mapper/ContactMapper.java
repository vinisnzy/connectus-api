package com.vinisnzy.connectus_api.domain.crm.mapper;

import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactResponse;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "metrics", ignore = true)
    @Mapping(target = "lastInteractionAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contact toEntity(CreateContactRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "metrics", ignore = true)
    @Mapping(target = "lastInteractionAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateContactRequest request, @MappingTarget Contact contact);

    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "customData", source = "customFields")
    @Mapping(target = "ticketsCount", constant = "0L")
    @Mapping(target = "appointmentsCount", constant = "0L")
    ContactResponse toResponse(Contact contact);
}
