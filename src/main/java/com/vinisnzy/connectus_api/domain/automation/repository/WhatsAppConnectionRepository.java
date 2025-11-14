package com.vinisnzy.connectus_api.domain.automation.repository;

import com.vinisnzy.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WhatsAppConnectionRepository extends JpaRepository<WhatsAppConnection, UUID> {
    List<WhatsAppConnection> findByCompany(Company company);
}