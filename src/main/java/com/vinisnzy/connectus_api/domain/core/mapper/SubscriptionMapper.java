package com.vinisnzy.connectus_api.domain.core.mapper;

import com.vinisnzy.connectus_api.domain.core.dto.request.CreateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.SubscriptionResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import com.vinisnzy.connectus_api.domain.core.entity.Subscription;
import com.vinisnzy.connectus_api.domain.core.entity.enums.BillingPeriod;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PlanMapper.class})
public interface SubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "companyId")
    @Mapping(target = "plan", source = "planId")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "billingPeriod", source = "billingPeriod")
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "trialEndsAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "usageData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subscription toEntity(CreateSubscriptionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "plan", source = "planId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "billingPeriod", source = "billingPeriod")
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "trialEndsAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "usageData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateSubscriptionRequest request, @MappingTarget Subscription subscription);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "billingPeriod", source = "billingPeriod")
    SubscriptionResponse toResponse(Subscription subscription);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default Plan mapPlan(java.util.UUID planId) {
        if (planId == null) return null;
        Plan plan = new Plan();
        plan.setId(planId);
        return plan;
    }

    default BillingPeriod mapBillingPeriod(String billingPeriod) {
        if (billingPeriod == null) return null;
        return BillingPeriod.valueOf(billingPeriod.toUpperCase());
    }

    default SubscriptionStatus mapSubscriptionStatus(String status) {
        if (status == null) return null;
        return SubscriptionStatus.valueOf(status.toUpperCase());
    }

    default String mapBillingPeriodToString(BillingPeriod billingPeriod) {
        if (billingPeriod == null) return null;
        return billingPeriod.name();
    }

    default String mapSubscriptionStatusToString(SubscriptionStatus status) {
        if (status == null) return null;
        return status.name();
    }
}
