package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.dto.request.CreateCompanyRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateCompanyRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.CompanyResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.mapper.CompanyMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.infra.utils.JsonUtils;
import com.vinisnzy.connectus_api.infra.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ActivityLogService activityLogService;
    private final CompanyMapper mapper;

    public CompanyResponse findById(UUID id) {
        Company company = findEntityById(id);
        return mapper.toResponse(company);
    }

    public Company findEntityById(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + id));
    }

    public CompanyResponse findByCnpj(String cnpj) {
        validateCnpj(cnpj);
        Company company = companyRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o cnpj: " + cnpj));
        return mapper.toResponse(company);
    }

    @Transactional
    public Company create(CreateCompanyRequest request) {
        validateCnpj(request.cnpj());
        if (Boolean.TRUE.equals(companyRepository.existsByCnpj(request.cnpj()))) {
            throw new IllegalArgumentException("Esse cnpj já está sendo utilizado");
        }

        Company company = new Company();
        company.setCnpj(request.cnpj());
        company.setName(request.name());

        // TODO: Send verification email

        company = companyRepository.save(company);

        activityLogService.log("ENTITY_CREATED", "Company", company.getId());

        return company;
    }

    @Transactional
    public Company update(UUID id, UpdateCompanyRequest updatedCompany) {
        Company company = findEntityById(id);

        validateCnpj(updatedCompany.cnpj());
        if (Boolean.TRUE.equals(companyRepository.existsByCnpj(updatedCompany.cnpj()))) {
            throw new IllegalArgumentException("Esse cnpj já está sendo utilizado");
        }

        mapper.updateEntity(updatedCompany, company);

        company = companyRepository.save(company);

        activityLogService.log("ENTITY_UPDATED", "Company", company.getId());

        return company;
    }

    @Transactional
    public void delete(UUID id) {
        // TODO: Implement soft delete or hard delete based on business rules
        // TODO: Clean up related data (users, contacts, messages, etc.)

        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Empresa não encontrada com o id: " + id);
        }

        activityLogService.log("ENTITY_DELETED", "Company", id);

        companyRepository.deleteById(id);
    }

    @Transactional
    public Company toggleActive(UUID id, boolean isActive) {
        Company company = findEntityById(id);

        // TODO: Add business logic (notify users, cancel active sessions, etc.)

        company.setIsActive(isActive);
        company = companyRepository.save(company);

        activityLogService.log("STATUS_CHANGED", "Company", company.getId());

        return company;
    }

    @Transactional
    public Company verify(UUID id) {
        Company company = findEntityById(id);

        // TODO: Add verification logic
        // TODO: Send notification to company admin

        company.setIsVerified(true);
        company = companyRepository.save(company);

        activityLogService.log("STATUS_CHANGED", "Company", company.getId());

        return company;
    }

    @Transactional
    public Company updateSettings(UUID id, String settingsJson) {
        Company company = findEntityById(id);

        Map<String, Object> settings = JsonUtils.mergeMapWithJsonString(company.getSettings(), settingsJson);
        company.setSettings(settings);
        company = companyRepository.save(company);

        activityLogService.log("ENTITY_UPDATED", "Company", company.getId());

        return company;
    }

    private void validateCnpj(String cnpj) {
        if (!ValidationUtils.validateCnpj(cnpj)) {
            throw new IllegalArgumentException("O CNPJ " + cnpj + " é inválido");
        }
    }

    // TODO: Add method to get company statistics
    // TODO: Add method to get company usage metrics
    // TODO: Add method to check if company has reached limits (based on plan)
}
