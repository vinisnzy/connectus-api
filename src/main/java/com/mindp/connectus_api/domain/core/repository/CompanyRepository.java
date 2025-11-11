package com.mindp.connectus_api.domain.core.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}