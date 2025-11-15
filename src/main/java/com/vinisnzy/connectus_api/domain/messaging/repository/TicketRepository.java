package com.vinisnzy.connectus_api.domain.messaging.repository;

import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Page<Ticket> findByCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);

    Page<Ticket> findByCompanyIdAndStatusOrderByPriorityDescCreatedAtDesc(
            UUID companyId,
            TicketStatus status,
            Pageable pageable
    );

    Page<Ticket> findByAssignedUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Ticket> findByContactIdOrderByCreatedAtDesc(UUID contactId, Pageable pageable);

    Optional<Ticket> findByCompanyIdAndTicketNumber(UUID companyId, Integer ticketNumber);

    @Query("""
                SELECT t
                FROM Ticket t
                WHERE t.company.id = :companyId
                  AND t.status = :status
                  AND t.assignedUser.id = :userId
                ORDER BY t.priority DESC, t.createdAt DESC
            """)
    Page<Ticket> findByCompanyIdAndStatusAndAssignedUserId(
            @Param("companyId") UUID companyId,
            @Param("status") TicketStatus status,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    // NATIVE QUERY – já estava correto, apenas mantido com companyId
    @Query(value = "SELECT * FROM messaging.tickets WHERE company_id = :companyId AND :tag = ANY(tags)", nativeQuery = true)
    Page<Ticket> findByCompanyIdAndTag(
            @Param("companyId") UUID companyId,
            @Param("tag") String tag,
            Pageable pageable
    );

    @Query("""
                SELECT t
                FROM Ticket t
                WHERE t.company.id = :companyId
                  AND t.status IN :statuses
                ORDER BY t.priority DESC, t.createdAt DESC
            """)
    Page<Ticket> findByCompanyIdAndStatusIn(
            @Param("companyId") UUID companyId,
            @Param("statuses") List<TicketStatus> statuses,
            Pageable pageable
    );

    @Query("""
                SELECT t
                FROM Ticket t
                WHERE t.company.id = :companyId
                  AND t.slaDeadline IS NOT NULL
                  AND t.slaDeadline < :deadline
                  AND t.status NOT IN ('RESOLVED', 'CLOSED')
                ORDER BY t.slaDeadline ASC
            """)
    List<Ticket> findOverdueBySlaDeadline(
            @Param("companyId") UUID companyId,
            @Param("deadline") ZonedDateTime deadline
    );

    @Query("""
                SELECT t
                FROM Ticket t
                WHERE t.company.id = :companyId
                  AND t.assignedUser IS NULL
                  AND t.status = :status
            """)
    Page<Ticket> findUnassignedByCompanyIdAndStatus(
            @Param("companyId") UUID companyId,
            @Param("status") TicketStatus status,
            Pageable pageable
    );

    @Query("""
                SELECT COUNT(t)
                FROM Ticket t
                WHERE t.company.id = :companyId
                  AND t.status = :status
            """)
    Long countByCompanyIdAndStatus(
            @Param("companyId") UUID companyId,
            @Param("status") TicketStatus status
    );

    Page<Ticket> findByIsArchivedFalseAndCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);
}
