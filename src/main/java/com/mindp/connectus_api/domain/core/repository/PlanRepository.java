package com.mindp.connectus_api.domain.core.repository;

import com.mindp.connectus_api.domain.core.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}