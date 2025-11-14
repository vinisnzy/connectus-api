package com.vinisnzy.connectus_api.domain.analytics.repository;

import com.vinisnzy.connectus_api.domain.analytics.entity.Notification;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserOrderByCreatedAt(User user, Pageable pageable);
    Long countByUserAndIsReadFalse(User user);
}