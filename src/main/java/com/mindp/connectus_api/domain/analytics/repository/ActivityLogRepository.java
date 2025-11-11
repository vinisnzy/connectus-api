package com.mindp.connectus_api.domain.analytics.repository;

import com.mindp.connectus_api.domain.analytics.entity.ActivityLog;
import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    Page<ActivityLog> findByCompanyOrderByCreatedAt(Company company, Pageable pageable);
    Page<ActivityLog> findByUserOrderByCreatedAt(User user, Pageable pageable);
}