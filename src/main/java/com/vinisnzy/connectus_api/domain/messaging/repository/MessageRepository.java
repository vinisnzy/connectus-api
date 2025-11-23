package com.vinisnzy.connectus_api.domain.messaging.repository;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.messaging.entity.Message;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByTicketOrderBySentAtAsc(Ticket ticket, Pageable pageable);

    Page<Message> findByCompanyOrderBySentAtDesc(Company company, Pageable pageable);

    Page<Message> findByTicketAndDirectionOrderBySentAtAsc(Ticket ticket, MessageDirection direction, Pageable pageable);

    List<Message> findByTicketAndIsReadFalseAndDirection(Ticket ticket, MessageDirection direction);

    Long countByCompanyIdAndIsReadFalseAndDirection(UUID companyId, MessageDirection direction);

    @Query("SELECT m FROM Message m WHERE m.company = :company AND m.sentAt BETWEEN :startDate AND :endDate ORDER BY m.sentAt DESC")
    Page<Message> findByCompanyAndDateRange(@Param("company") Company company,
                                            @Param("startDate") ZonedDateTime startDate,
                                            @Param("endDate") ZonedDateTime endDate,
                                            Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.ticket = :ticket AND m.isRead = false AND m.direction = :direction")
    Long countUnreadByTicketAndDirection(@Param("ticket") Ticket ticket, @Param("direction") MessageDirection direction);

    List<Message> findTop10ByTicketOrderBySentAtDesc(Ticket ticket);
}
