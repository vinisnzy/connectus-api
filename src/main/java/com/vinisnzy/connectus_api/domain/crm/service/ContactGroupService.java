package com.vinisnzy.connectus_api.domain.crm.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactGroupResponse;
import com.vinisnzy.connectus_api.domain.crm.entity.ContactGroup;
import com.vinisnzy.connectus_api.domain.crm.mapper.ContactGroupMapper;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactGroupRepository;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactGroupService {

    private final ContactGroupRepository contactGroupRepository;
    private final CompanyRepository companyRepository;
    private final ActivityLogService activityLogService;
    private final ContactGroupMapper mapper;
    private final ContactRepository contactRepository;

    public List<ContactGroupResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<ContactGroup> contactGroups = contactGroupRepository.findByCompanyId(companyId, pageable);
        return contactGroups.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ContactGroupResponse findById(UUID id) {
        ContactGroup contactGroup = getContactGroupOrThrow(id);
        validateGroupBelongsToCompany(contactGroup);
        return mapper.toResponse(contactGroup);
    }

    public ContactGroupResponse findByName(String name) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<ContactGroup> contactGroups = contactGroupRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, name);
        return contactGroups.stream()
                .findFirst()
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de contatos não encontrado com o nome: " + name));
    }

    @Transactional
    public ContactGroupResponse create(CreateContactGroupRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        validateNameUniqueness(request.name(), companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        ContactGroup contactGroup = mapper.toEntity(request);
        contactGroup.setCompany(company);
        contactGroup = contactGroupRepository.save(contactGroup);

        activityLogService.log("ENTITY_CREATED", "ContactGroup", contactGroup.getId());

        return mapper.toResponse(contactGroup);
    }

    @Transactional
    public ContactGroupResponse update(UUID id, UpdateContactGroupRequest updatedContactGroup) {
        ContactGroup contactGroup = getContactGroupOrThrow(id);

        validateGroupBelongsToCompany(contactGroup);
        validateNameUniqueness(updatedContactGroup.name(), contactGroup.getCompany().getId());
        validateGroupBelongsToCompany(contactGroup);

        mapper.updateEntity(updatedContactGroup, contactGroup);

        contactGroup = contactGroupRepository.save(contactGroup);

        activityLogService.log("ENTITY_UPDATED", "ContactGroup", contactGroup.getId());

        return mapper.toResponse(contactGroup);
    }

    @Transactional
    public void delete(UUID id) {
        ContactGroup contactGroup = getContactGroupOrThrow(id);

        validateGroupBelongsToCompany(contactGroup);

        contactRepository.removeGroupFromContacts(id);

        activityLogService.log("ENTITY_DELETED", "ContactGroup", id);

        contactGroupRepository.deleteById(id);
    }

    public long countContacts(UUID id) {
        return contactRepository.countContactsByGroup(id);
    }

    private void validateGroupBelongsToCompany(ContactGroup group) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!group.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("O grupo de contatos não pertence à empresa atual.");
        }
    }

    private ContactGroup getContactGroupOrThrow(UUID id) {
        return contactGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de contatos não encontrado com o id: " + id));
    }

    private void validateNameUniqueness(String name, UUID companyId) {
        List<ContactGroup> existingGroups = contactGroupRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, name);
        if (!existingGroups.isEmpty()) {
            throw new IllegalArgumentException("Grupo de contatos com o nome '" + name + "' já existe.");
        }
    }

    // TODO: Add method to duplicate a group
    // TODO: Add method to export group members
    // TODO: Add method to send bulk message to group
}
