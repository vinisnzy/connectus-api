package com.mindp.connectus_api.domain.messaging.entity;

import com.mindp.connectus_api.domain.core.entity.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "ticket_tags", schema = "messaging", uniqueConstraints = {
    @UniqueConstraint(name = "unique_tag_per_company", columnNames = {"company_id", "name"})
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 7)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
