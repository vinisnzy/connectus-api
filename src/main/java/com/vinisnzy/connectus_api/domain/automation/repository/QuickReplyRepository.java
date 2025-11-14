package com.vinisnzy.connectus_api.domain.automation.repository;

import com.vinisnzy.connectus_api.domain.automation.entity.QuickReply;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuickReplyRepository extends JpaRepository<QuickReply, UUID> {
    Page<QuickReply> findByCompany(Company company, Pageable pageable);
    List<QuickReply> findByCompanyAndNameContainingIgnoreCase(Company company, String name);
}
