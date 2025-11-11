package com.mindp.connectus_api.domain.crm.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.crm.entity.CustomField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomFieldRepository extends JpaRepository<CustomField, UUID> {
    Page<CustomField> findByCompany(Company company, Pageable pageable);
}