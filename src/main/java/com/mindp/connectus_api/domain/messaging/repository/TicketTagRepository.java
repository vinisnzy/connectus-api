package com.mindp.connectus_api.domain.messaging.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.messaging.entity.TicketTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketTagRepository extends JpaRepository<TicketTag, Integer> {
    Page<TicketTag> findByCompanyOrderByNameAsc(Company company, Pageable pageable);

    List<TicketTag> findByCompanyOrderByNameAsc(Company company);

    Optional<TicketTag> findByCompanyAndName(Company company, String name);

    boolean existsByCompanyAndName(Company company, String name);
}
