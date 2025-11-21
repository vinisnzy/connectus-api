package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Page<User> findByCompanyId(UUID companyId, Pageable pageable);
    List<User> findByCompanyId(UUID companyId);
    Page<User> findByRoleId(Integer roleId, Pageable pageable);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
    User findByPhone(String phone);
    Page<User> findByCompanyIdAndStatus(UUID companyId, UserStatus status, Pageable pageable);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    Page<User> findByIsMaster(Boolean isMaster, Pageable pageable);
}