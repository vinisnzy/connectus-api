package com.vinisnzy.connectus_api.domain.automation.entity;

import com.vinisnzy.connectus_api.domain.automation.entity.enums.WhatsAppConnectionStatus;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
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
@Table(name = "whatsapp_connections", schema = "automation", uniqueConstraints = {
    @UniqueConstraint(name = "unique_phone_per_company", columnNames = {"company_id", "phone_number"})
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_by_user_id")
    private User connectedByUser;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "phone_number_formatted", length = 30)
    private String phoneNumberFormatted;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "automation.whatsapp_connection_status")
    private WhatsAppConnectionStatus status = WhatsAppConnectionStatus.PENDING;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "qr_code_expires_at")
    private ZonedDateTime qrCodeExpiresAt;

    @Column(name = "qr_code_scanned_at")
    private ZonedDateTime qrCodeScannedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "session_data", columnDefinition = "jsonb")
    private Map<String, Object> sessionData;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String about;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "business_profile", columnDefinition = "jsonb")
    private Map<String, Object> businessProfile;

    @Column(name = "connected_at")
    private ZonedDateTime connectedAt;

    @Column(name = "disconnected_at")
    private ZonedDateTime disconnectedAt;

    @Column(name = "last_heartbeat_at")
    private ZonedDateTime lastHeartbeatAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_info", columnDefinition = "jsonb")
    private Map<String, Object> deviceInfo;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", insertable = false)
    private Map<String, Object> settings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", insertable = false)
    private Map<String, Object> stats;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
