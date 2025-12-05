package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findByCompanyId(UUID companyId);

    List<Role> findByIsSystemRoleTrue();

    Boolean existsByNameAndCompanyId(String name, UUID companyId);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.company WHERE r.id = :id")
    Optional<Role> findByIdWithCompany(@Param("id") Integer id);
}