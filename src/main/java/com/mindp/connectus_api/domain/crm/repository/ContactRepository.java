package com.mindp.connectus_api.domain.crm.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.crm.entity.Contact;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByCompanyOrderByLastInteractionAt(Company company, Pageable pageable);
    Page<Contact> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = "SELECT * FROM crm.contacts WHERE :tag = ANY(tags)", nativeQuery = true)
    Page<Contact> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT * FROM crm.contacts WHERE :groupId = ANY(groups)", nativeQuery = true)
    Page<Contact> findByGroup(@Param("group") UUID groupId, Pageable pageable);

    Page<Contact> findByIsBlocked(Boolean isBlocked, Pageable pageable);
}