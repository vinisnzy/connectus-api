package com.mindp.connectus_api.domain.analytics.entity;

import com.mindp.connectus_api.domain.analytics.entity.enums.NotificationType;
import com.mindp.connectus_api.domain.core.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "analytics.notification_type")
    private NotificationType type = NotificationType.INFO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "related_data", columnDefinition = "jsonb")
    private Map<String, Object> relatedData; // contexto adicional

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
