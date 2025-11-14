package com.vinisnzy.connectus_api.domain.messaging.entity;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageType;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.SenderType;
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
@Table(name = "messages", schema = "messaging")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "messaging.message_direction")
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, columnDefinition = "messaging.message_type")
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false, columnDefinition = "messaging.sender_type")
    private SenderType senderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by_user_id")
    private User sentByUser;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> content;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = ZonedDateTime.now();
        }
    }
}
