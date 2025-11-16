package com.vinisnzy.connectus_api.domain.core.entity;

import com.vinisnzy.connectus_api.domain.core.entity.enums.BillingPeriod;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "subscriptions", schema = "core")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "core.subscription_status")
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false, columnDefinition = "core.billing_period")
    private BillingPeriod billingPeriod = BillingPeriod.MONTHLY;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "started_at")
    private ZonedDateTime startedAt;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    @Column(name = "trial_ends_at")
    private ZonedDateTime trialEndsAt;

    @Column(name = "canceled_at")
    private ZonedDateTime canceledAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "usage_data", columnDefinition = "jsonb", insertable = false)
    private Map<String, Object> usageData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
        if (startedAt == null) {
            startedAt = ZonedDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
