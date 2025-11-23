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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactService Unit Tests")
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ContactMapper mapper;

    @InjectMocks
    private ContactService contactService;

    private UUID companyId;
    private UUID contactId;
    private Company company;
    private Contact contact;
    private ContactResponse contactResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        contactId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);
        company.setName("Test Company");

        contact = new Contact();
        contact.setId(contactId);
        contact.setCompany(company);
        contact.setName("John Doe");
        contact.setPhone("+5511999999999");
        contact.setEmail("john@example.com");

        contactResponse = ContactResponse.builder()
                .id(contactId)
                .name("John Doe")
                .phone("+5511999999999")
                .email("john@example.com")
                .build();
    }

    @Test
    @DisplayName("Should find all contacts for company")
    void shouldFindAllContacts() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Contact> contactPage = new PageImpl<>(List.of(contact));

            when(contactRepository.findAllOrdered(companyId, pageable)).thenReturn(contactPage);
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            List<ContactResponse> result = contactService.findAll(pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(contactResponse);
            verify(contactRepository).findAllOrdered(companyId, pageable);
            verify(mapper).toResponse(contact);
        }
    }

    @Test
    @DisplayName("Should find contact by id")
    void shouldFindContactById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            ContactResponse result = contactService.findById(contactId);

            // Assert
            assertThat(result).isEqualTo(contactResponse);
            verify(contactRepository).findById(contactId);
            verify(mapper).toResponse(contact);
        }
    }

    @Test
    @DisplayName("Should throw exception when contact not found")
    void shouldThrowExceptionWhenContactNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> contactService.findById(contactId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Contato não encontrado");

            verify(contactRepository).findById(contactId);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Test
    @DisplayName("Should find contact by phone")
    void shouldFindContactByPhone() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String phone = "+5511999999999";
            when(contactRepository.findByCompanyIdAndPhone(companyId, phone)).thenReturn(Optional.of(contact));
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            ContactResponse result = contactService.findByPhone(phone);

            // Assert
            assertThat(result).isEqualTo(contactResponse);
            verify(contactRepository).findByCompanyIdAndPhone(companyId, phone);
        }
    }

    @Test
    @DisplayName("Should find or create contact by phone - existing contact")
    void shouldFindExistingContactByPhone() {
        // Arrange
        String phone = "+5511999999999";
        when(contactRepository.findByCompanyIdAndPhone(companyId, phone)).thenReturn(Optional.of(contact));

        // Act
        Contact result = contactService.findOrCreateByPhone(phone, companyId);

        // Assert
        assertThat(result).isEqualTo(contact);
        verify(contactRepository).findByCompanyIdAndPhone(companyId, phone);
        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find or create contact by phone - create new contact")
    void shouldCreateNewContactWhenNotFound() {
        // Arrange
        String phone = "+5511988888888";
        when(contactRepository.findByCompanyIdAndPhone(companyId, phone)).thenReturn(Optional.empty());
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        // Act
        Contact result = contactService.findOrCreateByPhone(phone, companyId);

        // Assert
        assertThat(result).isEqualTo(contact);
        verify(contactRepository).findByCompanyIdAndPhone(companyId, phone);
        verify(companyRepository).findById(companyId);
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    @DisplayName("Should find contact by email")
    void shouldFindContactByEmail() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String email = "john@example.com";
            when(contactRepository.findByCompanyIdAndEmail(companyId, email)).thenReturn(Optional.of(contact));
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            ContactResponse result = contactService.findByEmail(email);

            // Assert
            assertThat(result).isEqualTo(contactResponse);
            verify(contactRepository).findByCompanyIdAndEmail(companyId, email);
        }
    }

    @Test
    @DisplayName("Should create contact successfully")
    void shouldCreateContact() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateContactRequest request = new CreateContactRequest(
                    "John Doe",
                    "+5511999999999",
                    "john@example.com",
                    null,
                    null
            );

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(mapper.toEntity(request)).thenReturn(contact);
            when(contactRepository.save(contact)).thenReturn(contact);
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            ContactResponse result = contactService.create(request);

            // Assert
            assertThat(result).isEqualTo(contactResponse);
            verify(companyRepository).findById(companyId);
            verify(contactRepository).save(contact);
            verify(mapper).toResponse(contact);
        }
    }

    @Test
    @DisplayName("Should update contact successfully")
    void shouldUpdateContact() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateContactRequest request = new UpdateContactRequest(
                    "Jane Doe",
                    "+5511988888888",
                    List.of("jane@example.com"),
                    null
            );

            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
            doNothing().when(mapper).updateEntity(request, contact);
            when(contactRepository.save(contact)).thenReturn(contact);
            when(mapper.toResponse(contact)).thenReturn(contactResponse);

            // Act
            ContactResponse result = contactService.update(contactId, request);

            // Assert
            assertThat(result).isEqualTo(contactResponse);
            verify(contactRepository).findById(contactId);
            verify(mapper).updateEntity(request, contact);
            verify(contactRepository).save(contact);
        }
    }

    @Test
    @DisplayName("Should delete contact successfully")
    void shouldDeleteContact() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
            doNothing().when(contactRepository).deleteById(contactId);

            // Act
            contactService.delete(contactId);

            // Assert
            verify(contactRepository).findById(contactId);
            verify(contactRepository).deleteById(contactId);
        }
    }

    @Test
    @DisplayName("Should throw exception when deleting contact from different company")
    void shouldThrowExceptionWhenDeletingContactFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(UUID.randomUUID());
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

            // Act & Assert
            assertThatThrownBy(() -> contactService.delete(contactId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Contato não pertence à empresa atual");

            verify(contactRepository, never()).deleteById(any());
        }
    }
}
