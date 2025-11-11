package com.mindp.connectus_api.domain.scheduling.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.scheduling.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Page<Service> findByCompanyOrderByNameAsc(Company company, Pageable pageable);

    Page<Service> findByCompanyAndIsActiveTrueOrderByNameAsc(Company company, Pageable pageable);

    List<Service> findByCompanyAndIsActiveTrueOrderByNameAsc(Company company);

    Page<Service> findByNameContainingIgnoreCaseAndCompany(String name, Company company, Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.company = :company AND s.isActive = true AND s.price IS NOT NULL ORDER BY s.price ASC")
    List<Service> findActiveServicesWithPriceOrderByPrice(@Param("company") Company company);

    @Query("SELECT COUNT(s) FROM Service s WHERE s.company = :company AND s.isActive = true")
    Long countActiveByCompany(@Param("company") Company company);
}
