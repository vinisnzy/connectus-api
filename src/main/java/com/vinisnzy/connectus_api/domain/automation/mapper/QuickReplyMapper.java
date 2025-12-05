package com.vinisnzy.connectus_api.domain.automation.mapper;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.QuickReplyResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.QuickReply;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuickReplyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "message", source = "messageContent")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "mediaType", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    QuickReply toEntity(CreateQuickReplyRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "mediaType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntity(UpdateQuickReplyRequest request, @MappingTarget QuickReply quickReply);

    @Mapping(target = "messageContent", source = "message")
    @Mapping(target = "usageCount", constant = "0")
    QuickReplyResponse toResponse(QuickReply quickReply);
}
