package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Page<Role> findByCompanyId(UUID companyId, Pageable pageable);
    Page<Role> findByCompanyIdAndIsSystemRoleFalse(UUID companyId, Boolean isSystemRole, Pageable pageable);
}