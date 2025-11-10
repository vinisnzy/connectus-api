package com.mindp.connectus_api.domain.messaging.entity;

import com.mindp.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.core.entity.User;
import com.mindp.connectus_api.domain.crm.entity.Contact;
import com.mindp.connectus_api.domain.messaging.entity.enums.ResolutionType;
import com.mindp.connectus_api.domain.messaging.entity.enums.TicketStatus;
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
@Table(name = "tickets", schema = "messaging")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "ticket_number", nullable = false)
    private Integer ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whatsapp_connection_id")
    private WhatsAppConnection whatsappConnection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "messaging.ticket_status")
    private TicketStatus status = TicketStatus.OPEN;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(length = 50)
    private String channel = "whatsapp";

    @Column(length = 50)
    private String category;

    @Column(name = "pending_until")
    private ZonedDateTime pendingUntil;

    @Column(name = "sla_deadline")
    private ZonedDateTime slaDeadline;

    @Column(name = "first_response_at")
    private ZonedDateTime firstResponseAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_type", columnDefinition = "messaging.resolution_type")
    private ResolutionType resolutionType;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(columnDefinition = "TEXT[]")
    private String[] tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @Column(name = "closed_at")
    private ZonedDateTime closedAt;

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
