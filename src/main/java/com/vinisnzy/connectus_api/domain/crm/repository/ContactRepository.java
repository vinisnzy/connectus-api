package com.vinisnzy.connectus_api.domain.crm.repository;

import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByCompanyIdOrderByLastInteractionAt(UUID companyId, Pageable pageable);

    Page<Contact> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Contact> findByCompanyIdAndPhone(UUID companyId, String phone);

    Optional<Contact> findByCompanyIdAndEmail(UUID companyId, String email);

    @Query(value = "SELECT * FROM crm.contacts WHERE :tag = ANY(tags)", nativeQuery = true)
    Page<Contact> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT * FROM crm.contacts WHERE :groupId = ANY(groups)", nativeQuery = true)
    Page<Contact> findByGroupId(@Param("group") UUID groupId, Pageable pageable);

    @Modifying
    @Query("""
                UPDATE Contact c
                SET c.groups = array_remove(c.groups, :groupId)
                WHERE :groupId = ANY(c.groups)
            """)
    void removeGroupFromContacts(@Param("groupId") UUID groupId);

    @Query("""
                SELECT COUNT(c)
                FROM Contact c
                WHERE :groupId = ANY(c.groups)
            """)
    long countContactsByGroup(@Param("groupId") UUID groupId);

    @Query("""
                SELECT c
                FROM Contact c
                WHERE c.company.id = :companyId
                ORDER BY COALESCE(c.lastInteractionAt, c.createdAt) DESC
            """)
    Page<Contact> findAllOrdered(@Param("companyId") UUID companyId, Pageable pageable);


    Page<Contact> findByIsBlocked(Boolean isBlocked, Pageable pageable);

    boolean existsByCompanyIdAndPhone(UUID companyId, String phone);
}