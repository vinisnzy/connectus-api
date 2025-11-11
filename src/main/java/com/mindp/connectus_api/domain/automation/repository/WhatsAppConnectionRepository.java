package com.mindp.connectus_api.domain.automation.repository;

import com.mindp.connectus_api.domain.automation.entity.WhatsAppConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WhatsAppConnectionRepository extends JpaRepository<WhatsAppConnection, UUID> {
}