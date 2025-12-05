package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    List<Plan> findAllByOrderByYearlyPriceDesc();
    List<Plan> findByIsActiveTrueOrderByYearlyPriceDesc();
    Optional<Plan> findByName(String name);
}