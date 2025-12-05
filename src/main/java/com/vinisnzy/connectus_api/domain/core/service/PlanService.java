package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.dto.request.CreatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdatePricingPlanRequest;
import com.vinisnzy.connectus_api.shared.dto.UpdateJsonRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdatePlanRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.PlanResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import com.vinisnzy.connectus_api.domain.core.mapper.PlanMapper;
import com.vinisnzy.connectus_api.domain.core.repository.PlanRepository;
import com.vinisnzy.connectus_api.domain.core.repository.SubscriptionRepository;
import com.vinisnzy.connectus_api.infra.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ActivityLogService activityLogService;

    private final PlanMapper mapper;

    public List<PlanResponse> findAll() {
        return planRepository.findAllByOrderByYearlyPriceDesc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PlanResponse> findAllActive() {
        return planRepository.findByIsActiveTrueOrderByYearlyPriceDesc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PlanResponse findById(UUID id) {
        Plan plan = getPlanByIdOrThrow(id);
        return mapper.toResponse(plan);
    }

    @Transactional
    public PlanResponse create(CreatePlanRequest request) {
        if (planRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("O nome do plano já existe");
        }
        validatePricing(request.monthlyPrice(), request.yearlyPrice());
        Plan plan = planRepository.save(mapper.toEntity(request));

        activityLogService.log("ENTITY_CREATED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    @Transactional
    public PlanResponse update(UUID id, UpdatePlanRequest request) {
        Plan plan = getPlanByIdOrThrow(id);

        // TODO: Validate that changes don't affect existing subscriptions negatively
        validatePricing(request.monthlyPrice(), request.yearlyPrice());

        mapper.updateEntity(request, plan);

        plan = planRepository.save(plan);

        activityLogService.log("ENTITY_UPDATED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    @Transactional
    public void delete(UUID id) {
        if (!planRepository.existsById(id)) {
            throw new IllegalArgumentException(("Plano não encontrado com o id: " + id));
        }
        if (planHaveActiveSubscriptions(id)) {
            throw new IllegalArgumentException("Não é possível deletar um plano que possui assinaturas ativas");
        }

        activityLogService.log("ENTITY_DELETED", "Plan", id);

        planRepository.deleteById(id);
    }

    @Transactional
    public PlanResponse toggleActive(UUID id, boolean isActive) {
        Plan plan = getPlanByIdOrThrow(id);

        // TODO: Add business logic (prevent deactivating if has active subscriptions)
        if (Boolean.TRUE.equals(plan.getIsActive()) && planHaveActiveSubscriptions(id)) {
            throw new IllegalArgumentException("Não é possível desativar um plano que possui assinaturas ativas");
        }
        plan.setIsActive(isActive);
        plan = planRepository.save(plan);

        activityLogService.log("STATUS_CHANGED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    @Transactional
    public PlanResponse updatePricing(UUID id, UpdatePricingPlanRequest request) {
        Plan plan = getPlanByIdOrThrow(id);

        validatePricing(request.monthlyPrice(), request.yearlyPrice());

        // TODO: Check impact on existing subscriptions

        plan.setMonthlyPrice(request.monthlyPrice());
        plan.setYearlyPrice(request.yearlyPrice());

        plan = planRepository.save(plan);

        activityLogService.log("ENTITY_UPDATED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    @Transactional
    public PlanResponse updateLimits(UUID id, UpdateJsonRequest limitsData) {
        Plan plan = getPlanByIdOrThrow(id);

        JsonUtils.validatePlanLimitsJson(limitsData.data());

        // TODO: Check impact on existing subscriptions

        plan.setLimits(limitsData.data());

        plan = planRepository.save(plan);

        activityLogService.log("ENTITY_UPDATED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    @Transactional
    public PlanResponse updateFeatures(UUID id, UpdateJsonRequest featuresData) {
        Plan plan = getPlanByIdOrThrow(id);

        JsonUtils.validatePlanFeaturesJson(featuresData.data());

        // TODO: Check impact on existing subscriptions

        plan.setFeatures(featuresData.data());

        plan = planRepository.save(plan);

        activityLogService.log("ENTITY_UPDATED", "Plan", plan.getId());

        return mapper.toResponse(plan);
    }

    private boolean planHaveActiveSubscriptions(UUID planId) {
        return subscriptionRepository.existsByPlanId(planId);
    }

    private void validatePricing(BigDecimal monthlyPrice, BigDecimal yearlyPrice) {
        if (monthlyPrice.compareTo(yearlyPrice != null ? yearlyPrice : monthlyPrice) > 0) {
            throw new IllegalArgumentException("O preço mensal não pode ser maior que o preço anual");
        }
    }

    private Plan getPlanByIdOrThrow(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o id: " + id));
    }

    // TODO: Add method to compare plans
    // TODO: Add method to get recommended plan based on usage
    // TODO: Add method to calculate upgrade/downgrade costs
}
