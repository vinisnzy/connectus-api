package com.vinisnzy.connectus_api.domain.messaging.mapper;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.UpdateTicketStatusRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketTagResponse;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.messaging.entity.TicketTag;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.ResolutionType;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.TicketStatus;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "contact", source = "contactId")
    @Mapping(target = "assignedUser", source = "assignedUserId")
    @Mapping(target = "whatsappConnection", ignore = true)
    @Mapping(target = "ticketNumber", ignore = true)
    @Mapping(target = "priority", constant = "0")
    @Mapping(target = "channel", constant = "whatsapp")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "isArchived", constant = "false")
    @Mapping(target = "pendingUntil", ignore = true)
    @Mapping(target = "slaDeadline", ignore = true)
    @Mapping(target = "firstResponseAt", ignore = true)
    @Mapping(target = "resolutionType", ignore = true)
    @Mapping(target = "resolutionNotes", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ticket toEntity(CreateTicketRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "whatsappConnection", ignore = true)
    @Mapping(target = "ticketNumber", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "channel", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "pendingUntil", ignore = true)
    @Mapping(target = "slaDeadline", ignore = true)
    @Mapping(target = "firstResponseAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    void updateEntity(UpdateTicketStatusRequest request, @MappingTarget Ticket ticket);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "resolutionType", source = "resolutionType")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "unreadCount", constant = "0L")
    @Mapping(target = "metadata", ignore = true)
    TicketResponse toResponse(Ticket ticket);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default Contact mapContact(java.util.UUID contactId) {
        if (contactId == null) return null;
        Contact contact = new Contact();
        contact.setId(contactId);
        return contact;
    }

    default User mapUser(java.util.UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default WhatsAppConnection mapWhatsAppConnection(java.util.UUID whatsappConnectionId) {
        if (whatsappConnectionId == null) return null;
        WhatsAppConnection connection = new WhatsAppConnection();
        connection.setId(whatsappConnectionId);
        return connection;
    }

    default TicketStatus mapTicketStatus(String status) {
        if (status == null) return null;
        return TicketStatus.valueOf(status.toUpperCase());
    }

    default String mapTicketStatusToString(TicketStatus status) {
        if (status == null) return null;
        return status.name();
    }

    default String mapResolutionTypeToString(ResolutionType resolutionType) {
        if (resolutionType == null) return null;
        return resolutionType.name();
    }

    default List<TicketTagResponse> mapTags(Set<TicketTag> tags) {
        if (tags == null || tags.isEmpty()) return List.of();
        return tags.stream()
                .map(tag -> new TicketTagResponse(
                        tag.getId(),
                        tag.getName(),
                        tag.getColor(),
                        tag.getDescription()
                ))
                .toList();
    }
}
