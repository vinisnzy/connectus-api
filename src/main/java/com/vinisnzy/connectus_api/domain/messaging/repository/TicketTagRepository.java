package com.vinisnzy.connectus_api.domain.messaging.repository;

import com.vinisnzy.connectus_api.domain.messaging.entity.TicketTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketTagRepository extends JpaRepository<TicketTag, Integer> {
    Page<TicketTag> findByCompanyIdOrderByNameAsc(UUID companyId, Pageable pageable);

    List<TicketTag> findByCompanyIdOrderByNameAsc(UUID companyId);

    Optional<TicketTag> findByCompanyIdAndName(UUID companyId, String name);

    boolean existsByCompanyIdAndName(UUID companyId, String name);
}
