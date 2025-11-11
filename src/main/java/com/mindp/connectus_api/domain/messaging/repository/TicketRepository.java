package com.mindp.connectus_api.domain.messaging.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.core.entity.User;
import com.mindp.connectus_api.domain.crm.entity.Contact;
import com.mindp.connectus_api.domain.messaging.entity.Ticket;
import com.mindp.connectus_api.domain.messaging.entity.enums.TicketStatus;
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
    Page<Ticket> findByCompanyOrderByCreatedAtDesc(Company company, Pageable pageable);

    Page<Ticket> findByCompanyAndStatusOrderByPriorityDescCreatedAtDesc(Company company, TicketStatus status, Pageable pageable);

    Page<Ticket> findByAssignedUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Ticket> findByContactOrderByCreatedAtDesc(Contact contact, Pageable pageable);

    Optional<Ticket> findByCompanyAndTicketNumber(Company company, Integer ticketNumber);

    @Query("SELECT t FROM Ticket t WHERE t.company = :company AND t.status = :status AND t.assignedUser = :user ORDER BY t.priority DESC, t.createdAt DESC")
    Page<Ticket> findByCompanyAndStatusAndAssignedUser(@Param("company") Company company,
                                                       @Param("status") TicketStatus status,
                                                       @Param("user") User user,
                                                       Pageable pageable);

    @Query(value = "SELECT * FROM messaging.tickets WHERE company_id = :companyId AND :tag = ANY(tags)", nativeQuery = true)
    Page<Ticket> findByCompanyAndTag(@Param("companyId") UUID companyId, @Param("tag") String tag, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.company = :company AND t.status IN :statuses ORDER BY t.priority DESC, t.createdAt DESC")
    Page<Ticket> findByCompanyAndStatusIn(@Param("company") Company company,
                                          @Param("statuses") List<TicketStatus> statuses,
                                          Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.company = :company AND t.slaDeadline IS NOT NULL AND t.slaDeadline < :deadline AND t.status NOT IN ('RESOLVED', 'CLOSED') ORDER BY t.slaDeadline ASC")
    List<Ticket> findOverdueBySlaDeadline(@Param("company") Company company, @Param("deadline") ZonedDateTime deadline);

    @Query("SELECT t FROM Ticket t WHERE t.assignedUser IS NULL AND t.company = :company AND t.status = :status")
    Page<Ticket> findUnassignedByCompanyAndStatus(@Param("company") Company company, @Param("status") TicketStatus status, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.company = :company AND t.status = :status")
    Long countByCompanyAndStatus(@Param("company") Company company, @Param("status") TicketStatus status);

    Page<Ticket> findByIsArchivedFalseAndCompanyOrderByCreatedAtDesc(Company company, Pageable pageable);
}
