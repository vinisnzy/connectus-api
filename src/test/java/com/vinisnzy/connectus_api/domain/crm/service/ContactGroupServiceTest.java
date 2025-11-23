package com.vinisnzy.connectus_api.domain.crm.service;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.crm.dto.request.CreateContactGroupRequest;
import com.vinisnzy.connectus_api.domain.crm.dto.response.ContactGroupResponse;
import com.vinisnzy.connectus_api.domain.crm.entity.ContactGroup;
import com.vinisnzy.connectus_api.domain.crm.mapper.ContactGroupMapper;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactGroupRepository;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactGroupService Unit Tests")
class ContactGroupServiceTest {

    @Mock
    private ContactGroupRepository contactGroupRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private ContactGroupMapper mapper;

    @InjectMocks
    private ContactGroupService contactGroupService;

    private UUID companyId;
    private UUID groupId;
    private Company company;
    private ContactGroup contactGroup;
    private ContactGroupResponse groupResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        groupId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        contactGroup = new ContactGroup();
        contactGroup.setId(groupId);
        contactGroup.setCompany(company);
        contactGroup.setName("VIP");

        groupResponse = ContactGroupResponse.builder()
                .id(groupId)
                .name("VIP")
                .build();
    }

    @Test
    @DisplayName("Should find all contact groups")
    void shouldFindAllContactGroups() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            Pageable pageable = PageRequest.of(0, 10);
            Page<ContactGroup> page = new PageImpl<>(List.of(contactGroup), pageable, 1);

            when(contactGroupRepository.findByCompanyId(companyId, pageable)).thenReturn(page);
            when(mapper.toResponse(contactGroup)).thenReturn(groupResponse);

            List<ContactGroupResponse> result = contactGroupService.findAll(pageable);

            assertThat(result).hasSize(1);
            verify(contactGroupRepository).findByCompanyId(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should create contact group")
    void shouldCreateContactGroup() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateContactGroupRequest request = new CreateContactGroupRequest("VIP", "VIP customers");

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(mapper.toEntity(request)).thenReturn(contactGroup);
            when(contactGroupRepository.save(contactGroup)).thenReturn(contactGroup);
            when(mapper.toResponse(contactGroup)).thenReturn(groupResponse);

            ContactGroupResponse result = contactGroupService.create(request);

            assertThat(result).isEqualTo(groupResponse);
            verify(contactGroupRepository).save(contactGroup);
        }
    }

    @Test
    @DisplayName("Should delete contact group")
    void shouldDeleteContactGroup() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(contactGroupRepository.findById(groupId)).thenReturn(Optional.of(contactGroup));

            contactGroupService.delete(groupId);

            verify(contactGroupRepository).deleteById(groupId);
        }
    }
}
