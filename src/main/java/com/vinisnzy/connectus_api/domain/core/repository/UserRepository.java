package com.vinisnzy.connectus_api.domain.core.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Page<User> findByCompany(Company company, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
    User findByPhone(String phone);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    Page<User> findByIsMaster(Boolean isMaster, Pageable pageable);
}