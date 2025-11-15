package com.vinisnzy.connectus_api.domain.messaging.mapper;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.SendMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.MessageResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.messaging.entity.Message;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageType;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.SenderType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "sentByUser", ignore = true)
    @Mapping(target = "direction", constant = "OUTBOUND")
    @Mapping(target = "messageType", source = "messageType")
    @Mapping(target = "senderType", constant = "USER")
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Message toEntity(SendMessageRequest request);

    @Mapping(target = "direction", source = "direction")
    @Mapping(target = "messageType", source = "messageType")
    @Mapping(target = "senderType", source = "senderType")
    @Mapping(target = "senderId", source = "sentByUser.id")
    @Mapping(target = "isFromMe", expression = "java(message.getDirection() == com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection.OUTBOUND)")
    MessageResponse toResponse(Message message);

    default Ticket mapTicket(java.util.UUID ticketId) {
        if (ticketId == null) return null;
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        return ticket;
    }

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default User mapUser(java.util.UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default MessageType mapMessageType(String messageType) {
        if (messageType == null) return null;
        return MessageType.valueOf(messageType.toUpperCase());
    }

    default String mapMessageDirectionToString(MessageDirection direction) {
        if (direction == null) return null;
        return direction.name();
    }

    default String mapMessageTypeToString(MessageType messageType) {
        if (messageType == null) return null;
        return messageType.name();
    }

    default String mapSenderTypeToString(SenderType senderType) {
        if (senderType == null) return null;
        return senderType.name();
    }
}
