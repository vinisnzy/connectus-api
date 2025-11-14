package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Page<Role> findByCompany(Company company, Pageable pageable);
    Page<Role> findByCompanyAndIsSystemRoleFalse(Company company, Boolean isSystemRole, Pageable pageable);
}