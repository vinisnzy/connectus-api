package com.mindp.connectus_api.domain.automation.repository;

import com.mindp.connectus_api.domain.automation.entity.QuickReply;
import com.mindp.connectus_api.domain.core.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuickReplyRepository extends JpaRepository<QuickReply, UUID> {
    Page<QuickReply> findByCompany(Company company, Pageable pageable);
}
