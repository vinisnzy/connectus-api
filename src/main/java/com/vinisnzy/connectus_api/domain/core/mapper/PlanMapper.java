package com.vinisnzy.connectus_api.domain.core.mapper;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.PlanResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Plan toEntity(CreatePlanRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdatePlanRequest request, @MappingTarget Plan plan);

    PlanResponse toResponse(Plan plan);
}
