package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Subscription;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByCompanyId(UUID companyId);
    Optional<Subscription> findByCompanyIdAndStatus(UUID companyId, SubscriptionStatus status);
    Optional<Subscription> findByCompanyIdAndStatusIn(UUID companyId, List<SubscriptionStatus> status);
    Page<Subscription> findByPlanId(UUID planId, Pageable pageable);
    Page<Subscription> findByStatus(SubscriptionStatus status, Pageable pageable);
    Boolean existsByPlanId(UUID planId);
}