package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.dto.request.CreateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateSubscriptionRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.SubscriptionResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import com.vinisnzy.connectus_api.domain.core.entity.Subscription;
import com.vinisnzy.connectus_api.domain.core.entity.enums.BillingPeriod;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import com.vinisnzy.connectus_api.domain.core.mapper.SubscriptionMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.PlanRepository;
import com.vinisnzy.connectus_api.domain.core.repository.SubscriptionRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final ActivityLogService activityLogService;
    private final SubscriptionMapper mapper;

    public List<SubscriptionResponse> findAll(Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findAll(pageable);
        return subscriptions.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public SubscriptionResponse findById(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);
        return mapper.toResponse(subscription);
    }

    public SubscriptionResponse findCurrentCompanySubscription() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Optional<Subscription> subscription = subscriptionRepository
                .findByCompanyIdAndStatusIn(companyId, List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL));
        if (subscription.isEmpty()) {
            throw new IllegalStateException("Inscrição ativa não encontrada para a empresa com o id: " + companyId);
        }
        return mapper.toResponse(subscription.get());
    }

    public List<SubscriptionResponse> findAllByCompanyId(UUID companyId) {
        List<Subscription> subscriptions = subscriptionRepository.findByCompanyId(companyId);
        return subscriptions.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public SubscriptionResponse create(CreateSubscriptionRequest request) {

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + request.companyId()));

        boolean hasActiveSubscription = subscriptionRepository.findByCompanyIdAndStatusIn(
                company.getId(),
                List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL)
        ).isPresent();

        if (hasActiveSubscription) {
            throw new IllegalArgumentException("A empresa já possui uma assinatura ativa");
        }

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o id: " + request.planId()));

        Subscription subscription = mapper.toEntity(request);

        subscription.setCompany(company);
        subscription.setPlan(plan);

        BigDecimal price;
        if (subscription.getBillingPeriod() == BillingPeriod.MONTHLY) {
            price = plan.getMonthlyPrice();
        } else if (subscription.getBillingPeriod() == BillingPeriod.YEARLY) {
            price = plan.getYearlyPrice();
        } else {
            price = plan.getMonthlyPrice(); // placeholder para custom
        }

        BigDecimal finalPrice = price.multiply(request.discountPercentage());

        subscription.setPrice(price);
        subscription.setFinalPrice(finalPrice);

        subscription.setStartedAt(ZonedDateTime.now());

        if (subscription.getBillingPeriod() == BillingPeriod.MONTHLY) {
            subscription.setExpiresAt(ZonedDateTime.now().plusMonths(1));
        } else if (subscription.getBillingPeriod() == BillingPeriod.YEARLY) {
            subscription.setExpiresAt(ZonedDateTime.now().plusYears(1));
        } else {
            subscription.setExpiresAt(ZonedDateTime.now().plusMonths(1)); // placeholder
        }

        if (Boolean.TRUE.equals(plan.getIsTrialEligible())) {
            subscription.setStatus(SubscriptionStatus.TRIAL);
            subscription.setTrialEndsAt(ZonedDateTime.now().plusDays(request.trialDays()));
        }

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("ENTITY_CREATED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse update(UUID id, UpdateSubscriptionRequest request) {
        Subscription subscription = getSubscriptionOrThrow(id);
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o id: " + request.planId()));

        // TODO: Validate updated data
        if (request.status() != null) {
            subscription.setStatus(request.status());
        }
        // TODO: Recalculate final price if price or discount changed
        if (request.billingPeriod() != null || request.discountPercentage() != null) {
            BillingPeriod billingPeriod = request.billingPeriod() != null ? request.billingPeriod() : subscription.getBillingPeriod();
            BigDecimal discountPercentage = request.discountPercentage() != null ? request.discountPercentage() : subscription.getDiscountPercentage();
            BigDecimal price;
            switch (billingPeriod) {
                case BillingPeriod.MONTHLY -> price = plan.getMonthlyPrice();
                case BillingPeriod.YEARLY -> price = plan.getYearlyPrice();
                default -> price = plan.getMonthlyPrice(); // placeholder para custom
            }

            BigDecimal finalPrice = price.multiply(discountPercentage);

            subscription.setDiscountPercentage(request.discountPercentage());
            subscription.setPrice(price);
            subscription.setFinalPrice(finalPrice);
        }

        if (request.billingPeriod() != null) {
            if (request.billingPeriod() == BillingPeriod.MONTHLY) {
                subscription.setExpiresAt(subscription.getStartedAt().plusMonths(1));
            } else if (request.billingPeriod() == BillingPeriod.YEARLY) {
                subscription.setExpiresAt(subscription.getStartedAt().plusYears(1));
            } else {
                subscription.setExpiresAt(subscription.getStartedAt().plusMonths(1)); // placeholder
            }
            subscription.setBillingPeriod(request.billingPeriod());
        }

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("ENTITY_UPDATED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse cancel(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        // TODO: Send notification to company

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCanceledAt(ZonedDateTime.now());

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("STATUS_CHANGED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse renew(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE && subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new IllegalStateException("A inscrição não pode ser renovada no estado atual");
        }

        BillingPeriod billingPeriod = subscription.getBillingPeriod();
        switch (billingPeriod) {
            case BillingPeriod.MONTHLY -> subscription.setExpiresAt(subscription.getExpiresAt().plusMonths(1));

            case BillingPeriod.YEARLY -> subscription.setExpiresAt(subscription.getExpiresAt().plusYears(1));

            default -> subscription.setExpiresAt(subscription.getExpiresAt().plusMonths(1)); // placeholder
        }

        // TODO: Process payment
        // TODO: Send confirmation notification

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("STATUS_CHANGED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse suspend(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        // TODO: Send notification

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("STATUS_CHANGED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse reactivate(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        if (subscription.getStatus() != SubscriptionStatus.SUSPENDED) {
            throw new IllegalStateException("A inscrição não está suspensa e não pode ser reativada");
        }
        // TODO: Process pending payments if needed
        // TODO: Send confirmation notification

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscription = subscriptionRepository.save(subscription);

        activityLogService.log("STATUS_CHANGED", "Subscription", subscription.getId());

        return mapper.toResponse(subscription);
    }

    public boolean isExpired(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        return subscription.getExpiresAt().isBefore(ZonedDateTime.now());
    }

    public boolean isInTrial(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);

        return subscription.getTrialEndsAt() != null && subscription.getTrialEndsAt().isAfter(ZonedDateTime.now());
    }

    public long getDaysUntilExpiration(UUID id) {
        Subscription subscription = getSubscriptionOrThrow(id);
        return ChronoUnit.DAYS.between(ZonedDateTime.now(), subscription.getExpiresAt());
    }

    private Subscription getSubscriptionOrThrow(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscrição não encontrada com o id: " + id));
    }

    // TODO: Add method to process expired subscriptions (scheduled job)
    // TODO: Add method to send expiration warnings
    // TODO: Add method to update usage data
    // TODO: Add method to check if feature is available
    // TODO: Add method to check if limit is reached
}
