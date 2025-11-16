package com.vinisnzy.connectus_api.domain.scheduling.repository;

import com.vinisnzy.connectus_api.domain.scheduling.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Page<Service> findByCompanyIdOrderByNameAsc(UUID companyId, Pageable pageable);

    Page<Service> findByCompanyIdAndIsActiveTrueOrderByNameAsc(UUID companyId, Pageable pageable);

    List<Service> findByCompanyIdAndIsActiveTrueOrderByNameAsc(UUID companyId);

    Page<Service> findByNameContainingIgnoreCaseAndCompanyId(String name, UUID companyId, Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.company.id = :companyId AND s.isActive = true AND s.price IS NOT NULL ORDER BY s.price ASC")
    List<Service> findActiveServicesWithPriceOrderByPrice(@Param("companyId") UUID companyId);

    @Query("SELECT COUNT(s) FROM Service s WHERE s.company.id = :companyId AND s.isActive = true")
    Long countActiveByCompanyId(@Param("companyId") UUID companyId);
}
