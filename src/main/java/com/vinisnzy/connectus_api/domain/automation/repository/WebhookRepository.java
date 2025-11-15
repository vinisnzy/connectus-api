package com.vinisnzy.connectus_api.domain.automation.repository;

import com.vinisnzy.connectus_api.domain.automation.entity.Webhook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
    Page<Webhook> findByCompanyId(UUID companyId, Pageable pageable);
}