package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByCompanyId(UUID companyId);
    List<Role> findByIsSystemRole();
    Boolean existsByNameAndCompanyId(String name, UUID companyId);
}