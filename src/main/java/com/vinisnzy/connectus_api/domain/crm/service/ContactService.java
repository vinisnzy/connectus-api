package com.vinisnzy.connectus_api.domain.crm.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.request.UpdateContactRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactResponse;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.mapper.ContactMapper;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    private final ContactMapper mapper;

    public List<ContactResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Contact> contacts = contactRepository.findAllOrdered(companyId, pageable);

        return contacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ContactResponse findById(UUID id) {
        Contact contact = getContactOrThrow(id);
        validateContactBelongsToCompany(contact);
        return mapper.toResponse(contact);
    }

    public ContactResponse findByPhone(String phone) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Contact contact = contactRepository.findByCompanyIdAndPhone(companyId, phone)
                .orElseThrow(() -> new EntityNotFoundException("Contato com não encontrado com o número de telefone: " + phone));
        return mapper.toResponse(contact);
    }

    public ContactResponse findByEmail(String email) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Contact contact = contactRepository.findByCompanyIdAndEmail(companyId, email)
                .orElseThrow(() -> new EntityNotFoundException("Contato com não encontrado com o número de telefone: " + email));
        return mapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse create(CreateContactRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        if (contactRepository.existsByCompanyIdAndPhone(companyId, request.phone())) {
            throw new IllegalStateException("Contato com o mesmo telefone já existe.");
        }

        Contact contact = mapper.toEntity(request);
        Company company = companyRepository.findById(companyId)
                .orElseThrow((() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId)));
        contact.setCompany(company);

        if (request.tags() != null && !request.tags().isEmpty()) {
            contact.setTags(request.tags().toArray(new String[0]));
        }

        if (request.customData() != null) {
            contact.setCustomFields(request.customData());
        }

        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse update(UUID id, UpdateContactRequest updatedContact) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        mapper.updateEntity(updatedContact, contact);

        if (updatedContact.tags() != null) {
            contact.setTags(updatedContact.tags().toArray(new String[0]));
        }

        if (updatedContact.customData() != null) {
            contact.setCustomFields(updatedContact.customData());
        }

        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public void delete(UUID id) {
        Contact contact = getContactOrThrow(id);
        validateContactBelongsToCompany(contact);

        contactRepository.deleteById(id);
    }

    @Transactional
    public ContactResponse toggleBlock(UUID id, boolean isBlocked) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        contact.setIsBlocked(isBlocked);
        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public void updateLastInteraction(UUID id) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        contact.setLastInteractionAt(ZonedDateTime.now());
        contactRepository.save(contact);
    }

    @Transactional
    public ContactResponse addTags(UUID id, String[] tags) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        String[] existingTags = contact.getTags() != null ? contact.getTags() : new String[0];
        List<String> mergedTags = new ArrayList<>(List.of(existingTags));

        for (String tag : tags) {
            if (!mergedTags.contains(tag)) {
                mergedTags.add(tag);
            }
        }

        contact.setTags(mergedTags.toArray(new String[0]));
        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse removeTags(UUID id, String[] tags) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        if (contact.getTags() != null) {
            List<String> existingTags = new ArrayList<>(List.of(contact.getTags()));
            existingTags.removeAll(List.of(tags));
            contact.setTags(existingTags.toArray(new String[0]));
        }

        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse addToGroups(UUID id, UUID[] groupIds) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        UUID[] existingGroups = contact.getGroups() != null ? contact.getGroups() : new UUID[0];
        List<UUID> mergedGroups = new ArrayList<>(List.of(existingGroups));

        for (UUID groupId : groupIds) {
            if (!mergedGroups.contains(groupId)) {
                mergedGroups.add(groupId);
            }
        }

        contact.setGroups(mergedGroups.toArray(new UUID[0]));
        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    @Transactional
    public ContactResponse removeFromGroups(UUID id, UUID[] groupIds) {
        Contact contact = getContactOrThrow(id);

        validateContactBelongsToCompany(contact);

        if (contact.getGroups() != null) {
            List<UUID> existingGroups = new ArrayList<>(List.of(contact.getGroups()));
            existingGroups.removeAll(List.of(groupIds));
            contact.setGroups(existingGroups.toArray(new UUID[0]));
        }

        contact = contactRepository.save(contact);
        return mapper.toResponse(contact);
    }

    public List<ContactResponse> findByTag(String tag, Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByTag(tag, pageable);
        return contacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ContactResponse> findByGroup(UUID groupId, Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByGroupId(groupId, pageable);
        return contacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<ContactResponse> search(String query, Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByNameContainingIgnoreCase(query, pageable);
        return contacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public List<ContactResponse> importContacts(List<Contact> contacts) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        List<Contact> validContacts = contacts.stream()
                .filter(contact -> !contactRepository.existsByCompanyIdAndPhone(companyId, contact.getPhone()))
                .peek(contact -> contact.setCompany(company))
                .toList();

        List<Contact> savedContacts = contactRepository.saveAll(validContacts);
        return savedContacts.stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Contact getContactOrThrow(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com o id: " + id));
    }

    private void validateContactBelongsToCompany(Contact contact) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!contact.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Contato não pertence à empresa atual.");
        }
    }
}
