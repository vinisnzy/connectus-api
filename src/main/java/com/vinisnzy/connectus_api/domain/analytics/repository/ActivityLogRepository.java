package com.vinisnzy.connectus_api.domain.analytics.repository;

import com.vinisnzy.connectus_api.domain.analytics.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    Page<ActivityLog> findByCompanyIdOrderByCreatedAt(UUID companyId, Pageable pageable);
    Page<ActivityLog> findByUserIdOrderByCreatedAt(UUID userId, Pageable pageable);
}