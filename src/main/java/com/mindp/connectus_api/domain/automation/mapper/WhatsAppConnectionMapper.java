package com.mindp.connectus_api.domain.automation.mapper;

import com.mindp.connectus_api.domain.automation.dto.request.CreateWhatsAppConnectionRequest;
import com.mindp.connectus_api.domain.automation.dto.response.WhatsAppConnectionResponse;
import com.mindp.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.mindp.connectus_api.domain.automation.entity.enums.WhatsAppConnectionStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WhatsAppConnectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "connectedByUser", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "phoneNumberFormatted", ignore = true)
    @Mapping(target = "displayName", source = "connectionName")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "qrCode", ignore = true)
    @Mapping(target = "qrCodeExpiresAt", ignore = true)
    @Mapping(target = "qrCodeScannedAt", ignore = true)
    @Mapping(target = "sessionData", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "about", ignore = true)
    @Mapping(target = "businessProfile", ignore = true)
    @Mapping(target = "connectedAt", ignore = true)
    @Mapping(target = "disconnectedAt", ignore = true)
    @Mapping(target = "lastHeartbeatAt", ignore = true)
    @Mapping(target = "deviceInfo", ignore = true)
    @Mapping(target = "webhookUrl", ignore = true)
    @Mapping(target = "settings", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "lastError", ignore = true)
    @Mapping(target = "errorCount", constant = "0")
    @Mapping(target = "retryCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WhatsAppConnection toEntity(CreateWhatsAppConnectionRequest request);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "connectionName", source = "displayName")
    @Mapping(target = "lastConnectedAt", source = "connectedAt")
    WhatsAppConnectionResponse toResponse(WhatsAppConnection whatsAppConnection);

    default String mapWhatsAppConnectionStatusToString(WhatsAppConnectionStatus status) {
        if (status == null) return null;
        return status.name();
    }
}
