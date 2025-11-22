package com.vinisnzy.connectus_api.domain.scheduling.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateServiceRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.ServiceResponse;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Service;
import com.vinisnzy.connectus_api.domain.scheduling.mapper.ServiceMapper;
import com.vinisnzy.connectus_api.domain.scheduling.repository.ServiceRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ServiceRepository serviceRepository;
    private final CompanyRepository companyRepository;
    private final ServiceMapper mapper;

    public List<ServiceResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Service> services = serviceRepository.findByCompanyIdOrderByNameAsc(companyId, pageable);
        return services.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ServiceResponse> findAllActive() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<Service> services = serviceRepository.findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId);
        return services.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ServiceResponse> findAllActive(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Service> services =
                serviceRepository.findByCompanyIdAndIsActiveTrueOrderByNameAsc(companyId, pageable);
        return services.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ServiceResponse findById(UUID id) {
        Service service = getServiceOrThrow(id);
        validateServiceBelongsToCompany(service);
        return mapper.toResponse(service);
    }

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        Service service = mapper.toEntity(request);
        service.setCompany(company);

        service = serviceRepository.save(service);
        return mapper.toResponse(service);
    }

    @Transactional
    public ServiceResponse update(UUID id, UpdateServiceRequest request) {
        Service service = getServiceOrThrow(id);

        validateServiceBelongsToCompany(service);

        mapper.updateEntity(request, service);
        service = serviceRepository.save(service);
        return mapper.toResponse(service);
    }

    @Transactional
    public void delete(UUID id) {
        Service service = getServiceOrThrow(id);

        validateServiceBelongsToCompany(service);
        serviceRepository.deleteById(id);
    }

    @Transactional
    public ServiceResponse toggleActive(UUID id, boolean isActive) {
        Service service = getServiceOrThrow(id);

        validateServiceBelongsToCompany(service);

        service.setIsActive(isActive);
        service = serviceRepository.save(service);
        return mapper.toResponse(service);
    }

    @Transactional
    public ServiceResponse clone(UUID id, String newName) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        Service originalService = getServiceOrThrow(id);

        validateServiceBelongsToCompany(originalService);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        Service newService = new Service();
        newService.setCompany(company);
        newService.setName(newName);
        newService.setDescription(originalService.getDescription());
        newService.setDurationMinutes(originalService.getDurationMinutes());
        newService.setPrice(originalService.getPrice());
        newService.setBufferTimeMinutes(originalService.getBufferTimeMinutes());
        newService.setIsActive(originalService.getIsActive());

        newService = serviceRepository.save(newService);
        return mapper.toResponse(newService);
    }

    public List<ServiceResponse> search(String query, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Service> services = serviceRepository.findByNameContainingIgnoreCaseAndCompanyId(query, companyId, pageable);
        return services.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ServiceResponse> findActiveWithPrice() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<Service> services = serviceRepository.findActiveServicesWithPriceOrderByPrice(companyId);
        return services.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Long countActive() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return serviceRepository.countActiveByCompanyId(companyId);
    }

    private Service getServiceOrThrow(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado com o id: " + id));
    }

    private void validateServiceBelongsToCompany(Service service) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!service.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Serviço não pertence à empresa atual.");
        }
    }
}
