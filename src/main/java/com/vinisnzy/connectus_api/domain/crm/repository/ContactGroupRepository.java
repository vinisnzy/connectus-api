package com.vinisnzy.connectus_api.domain.crm.repository;

import com.vinisnzy.connectus_api.domain.crm.entity.ContactGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactGroupRepository extends JpaRepository<ContactGroup, UUID> {
    Page<ContactGroup> findByCompanyId(UUID companyId, Pageable pageable);
    Page<ContactGroup> findByNameContainingIgnoreCase(String name, Pageable pageable);
}