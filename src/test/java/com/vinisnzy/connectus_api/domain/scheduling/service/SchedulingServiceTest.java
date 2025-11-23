package com.vinisnzy.connectus_api.domain.scheduling.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.ServiceResponse;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Service;
import com.vinisnzy.connectus_api.domain.scheduling.mapper.ServiceMapper;
import com.vinisnzy.connectus_api.domain.scheduling.repository.ServiceRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SchedulingService Unit Tests")
class SchedulingServiceTest {

    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ServiceMapper mapper;

    @InjectMocks
    private SchedulingService schedulingService;

    private UUID companyId;
    private UUID serviceId;
    private Company company;
    private Service service;
    private ServiceResponse serviceResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        service = new Service();
        service.setId(serviceId);
        service.setCompany(company);
        service.setName("Haircut");
        service.setDurationMinutes(60);
        service.setPrice(BigDecimal.valueOf(50.00));
        service.setIsActive(true);

        serviceResponse = ServiceResponse.builder()
                .id(serviceId)
                .name("Haircut")
                .duration(60)
                .build();
    }

    @Test
    @DisplayName("Should find all services")
    void shouldFindAllServices() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            Pageable pageable = PageRequest.of(0, 10);

            when(serviceRepository.findByCompanyIdOrderByNameAsc(companyId, pageable))
                    .thenReturn(new PageImpl<>(List.of(service), pageable, 1));
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            List<ServiceResponse> result = schedulingService.findAll(pageable);

            assertThat(result).hasSize(1);
            verify(serviceRepository).findByCompanyIdOrderByNameAsc(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should find all active services")
    void shouldFindAllActiveServices() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            when(serviceRepository.findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId))
                    .thenReturn(List.of(service));
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            List<ServiceResponse> result = schedulingService.findAllActive();

            assertThat(result).hasSize(1);
            verify(serviceRepository).findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId);
        }
    }

    @Test
    @DisplayName("Should find all active services with pagination")
    void shouldFindAllActiveServicesWithPagination() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            Pageable pageable = PageRequest.of(0, 10);

            when(serviceRepository.findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId, pageable))
                    .thenReturn(new PageImpl<>(List.of(service), pageable, 1));
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            List<ServiceResponse> result = schedulingService.findAllActive(pageable);

            assertThat(result).hasSize(1);
            verify(serviceRepository).findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should find service by id")
    void shouldFindServiceById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.findById(serviceId);

            assertThat(result).isEqualTo(serviceResponse);
        }
    }

    @Test
    @DisplayName("Should create service")
    void shouldCreateService() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateServiceRequest request = new CreateServiceRequest(companyId, "Haircut", "Basic haircut", 60, BigDecimal.valueOf(50.00), 0);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(mapper.toEntity(request)).thenReturn(service);
            when(serviceRepository.save(service)).thenReturn(service);
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.create(request);

            assertThat(result).isEqualTo(serviceResponse);
            verify(serviceRepository).save(service);
        }
    }

    @Test
    @DisplayName("Should throw exception when service not found")
    void shouldThrowExceptionWhenServiceNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schedulingService.findById(serviceId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should return empty list when no active services found")
    void shouldReturnEmptyListWhenNoActiveServicesFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId)).thenReturn(List.of());

            List<ServiceResponse> result = schedulingService.findAllActive();

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should update service successfully")
    void shouldUpdateService() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(serviceRepository.save(service)).thenReturn(service);
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.update(serviceId, null);

            assertThat(result).isEqualTo(serviceResponse);
            verify(serviceRepository).save(service);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating service from different company")
    void shouldThrowExceptionWhenUpdatingServiceFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UUID differentCompanyId = UUID.randomUUID();
            Company differentCompany = new Company();
            differentCompany.setId(differentCompanyId);
            service.setCompany(differentCompany);

            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

            assertThatThrownBy(() -> schedulingService.update(serviceId, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("não pertence à empresa");
        }
    }

    @Test
    @DisplayName("Should delete service successfully")
    void shouldDeleteService() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

            schedulingService.delete(serviceId);

            verify(serviceRepository).deleteById(serviceId);
        }
    }

    @Test
    @DisplayName("Should throw exception when deleting service from different company")
    void shouldThrowExceptionWhenDeletingServiceFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UUID differentCompanyId = UUID.randomUUID();
            Company differentCompany = new Company();
            differentCompany.setId(differentCompanyId);
            service.setCompany(differentCompany);

            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

            assertThatThrownBy(() -> schedulingService.delete(serviceId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("não pertence à empresa");
        }
    }

    @Test
    @DisplayName("Should toggle service active status to true")
    void shouldToggleServiceActiveToTrue() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            service.setIsActive(false);

            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(serviceRepository.save(service)).thenReturn(service);
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.toggleActive(serviceId, true);

            assertThat(result).isEqualTo(serviceResponse);
            assertThat(service.getIsActive()).isTrue();
            verify(serviceRepository).save(service);
        }
    }

    @Test
    @DisplayName("Should toggle service active status to false")
    void shouldToggleServiceActiveToFalse() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            service.setIsActive(true);

            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(serviceRepository.save(service)).thenReturn(service);
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.toggleActive(serviceId, false);

            assertThat(result).isEqualTo(serviceResponse);
            assertThat(service.getIsActive()).isFalse();
            verify(serviceRepository).save(service);
        }
    }

    @Test
    @DisplayName("Should clone service successfully")
    void shouldCloneService() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String newName = "Haircut Premium";
            Service clonedService = new Service();
            clonedService.setId(UUID.randomUUID());
            clonedService.setCompany(company);
            clonedService.setName(newName);
            clonedService.setDurationMinutes(service.getDurationMinutes());
            clonedService.setPrice(service.getPrice());

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(serviceRepository.save(any(Service.class))).thenReturn(clonedService);
            when(mapper.toResponse(clonedService)).thenReturn(serviceResponse);

            ServiceResponse result = schedulingService.clone(serviceId, newName);

            assertThat(result).isEqualTo(serviceResponse);
            verify(serviceRepository).save(any(Service.class));
        }
    }

    @Test
    @DisplayName("Should search services by name")
    void shouldSearchServicesByName() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String query = "hair";
            Page<Service> servicePage = new PageImpl<>(List.of(service));

            when(serviceRepository.findByNameContainingIgnoreCaseAndCompanyId(eq(query), eq(companyId), any()))
                    .thenReturn(servicePage);
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            List<ServiceResponse> result = schedulingService.search(query, PageRequest.of(0, 10));

            assertThat(result).hasSize(1);
            verify(serviceRepository).findByNameContainingIgnoreCaseAndCompanyId(eq(query), eq(companyId), any());
        }
    }

    @Test
    @DisplayName("Should return empty list when search finds no results")
    void shouldReturnEmptyListWhenSearchFindsNoResults() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String query = "nonexistent";
            Page<Service> servicePage = new PageImpl<>(List.of());

            when(serviceRepository.findByNameContainingIgnoreCaseAndCompanyId(eq(query), eq(companyId), any()))
                    .thenReturn(servicePage);

            List<ServiceResponse> result = schedulingService.search(query, PageRequest.of(0, 10));

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should find active services with price")
    void shouldFindActiveServicesWithPrice() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.findActiveServicesWithPriceOrderByPrice(companyId))
                    .thenReturn(List.of(service));
            when(mapper.toResponse(service)).thenReturn(serviceResponse);

            List<ServiceResponse> result = schedulingService.findActiveWithPrice();

            assertThat(result).hasSize(1);
            verify(serviceRepository).findActiveServicesWithPriceOrderByPrice(companyId);
        }
    }

    @Test
    @DisplayName("Should count active services")
    void shouldCountActiveServices() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.countActiveByCompanyId(companyId)).thenReturn(5L);

            Long result = schedulingService.countActive();

            assertThat(result).isEqualTo(5L);
            verify(serviceRepository).countActiveByCompanyId(companyId);
        }
    }

    @Test
    @DisplayName("Should return zero when no active services")
    void shouldReturnZeroWhenNoActiveServices() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(serviceRepository.countActiveByCompanyId(companyId)).thenReturn(0L);

            Long result = schedulingService.countActive();

            assertThat(result).isZero();
        }
    }

    @Test
    @DisplayName("Should throw exception when creating service with non-existent company")
    void shouldThrowExceptionWhenCreatingServiceWithNonExistentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateServiceRequest request = new CreateServiceRequest(companyId, "Haircut", "Basic haircut", 60, BigDecimal.valueOf(50.00), 0);

            when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schedulingService.create(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Empresa não encontrada");
        }
    }
}
