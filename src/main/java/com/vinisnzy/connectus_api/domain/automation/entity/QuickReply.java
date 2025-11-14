package com.vinisnzy.connectus_api.domain.automation.entity;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "quick_replies", schema = "automation", uniqueConstraints = {
    @UniqueConstraint(name = "unique_shortcut_per_company", columnNames = {"company_id", "shortcut"})
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuickReply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 50)
    private String shortcut;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(length = 50)
    private String category;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(name = "media_type", length = 20)
    private String mediaType; // image, video, document

    @Column(name = "is_active")
    private Boolean isActive = true;

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
