package com.vinisnzy.connectus_api.domain.messaging.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketTagRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.UpdateTicketTagRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketTagResponse;
import com.vinisnzy.connectus_api.domain.messaging.entity.TicketTag;
import com.vinisnzy.connectus_api.domain.messaging.mapper.TicketTagMapper;
import com.vinisnzy.connectus_api.domain.messaging.repository.TicketTagRepository;
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
@DisplayName("TicketTagService Unit Tests")
class TicketTagServiceTest {

    @Mock
    private TicketTagRepository ticketTagRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private TicketTagMapper mapper;

    @InjectMocks
    private TicketTagService ticketTagService;

    private UUID companyId;
    private Company company;
    private TicketTag ticketTag;
    private TicketTagResponse ticketTagResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);
        company.setName("Test Company");

        ticketTag = new TicketTag();
        ticketTag.setId(1);
        ticketTag.setCompany(company);
        ticketTag.setName("Urgent");
        ticketTag.setColor("#FF0000");
        ticketTag.setDescription("Urgent tickets");

        ticketTagResponse = new TicketTagResponse(1, "Urgent", "#FF0000", "Urgent tickets");
    }

    @Test
    @DisplayName("Should find all ticket tags for company")
    void shouldFindAllTicketTags() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketTagRepository.findByCompanyIdOrderByNameAsc(companyId)).thenReturn(List.of(ticketTag));
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            List<TicketTagResponse> result = ticketTagService.findAll();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).findByCompanyIdOrderByNameAsc(companyId);
            verify(mapper).toResponse(ticketTag);
        }
    }

    @Test
    @DisplayName("Should find all ticket tags paginated")
    void shouldFindAllTicketTagsPaginated() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<TicketTag> tagPage = new PageImpl<>(List.of(ticketTag));

            when(ticketTagRepository.findByCompanyIdOrderByNameAsc(companyId, pageable)).thenReturn(tagPage);
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            List<TicketTagResponse> result = ticketTagService.findAll(pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).findByCompanyIdOrderByNameAsc(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should find ticket tag by id")
    void shouldFindTicketTagById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(ticketTag));
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            TicketTagResponse result = ticketTagService.findById(1);

            // Assert
            assertThat(result).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).findById(1);
            verify(mapper).toResponse(ticketTag);
        }
    }

    @Test
    @DisplayName("Should throw exception when ticket tag not found")
    void shouldThrowExceptionWhenTicketTagNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketTagRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> ticketTagService.findById(1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Tag não encontrada");

            verify(ticketTagRepository).findById(1);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Test
    @DisplayName("Should find ticket tag by name")
    void shouldFindTicketTagByName() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketTagRepository.findByCompanyIdAndName(companyId, "Urgent")).thenReturn(Optional.of(ticketTag));
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            TicketTagResponse result = ticketTagService.findByName("Urgent");

            // Assert
            assertThat(result).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).findByCompanyIdAndName(companyId, "Urgent");
        }
    }

    @Test
    @DisplayName("Should create ticket tag successfully")
    void shouldCreateTicketTag() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateTicketTagRequest request = new CreateTicketTagRequest(companyId, "Urgent", "#FF0000", "Urgent tickets");

            when(ticketTagRepository.existsByCompanyIdAndName(companyId, "Urgent")).thenReturn(false);
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(mapper.toEntity(request)).thenReturn(ticketTag);
            when(ticketTagRepository.save(ticketTag)).thenReturn(ticketTag);
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            TicketTagResponse result = ticketTagService.create(request);

            // Assert
            assertThat(result).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).existsByCompanyIdAndName(companyId, "Urgent");
            verify(companyRepository).findById(companyId);
            verify(ticketTagRepository).save(ticketTag);
            verify(mapper).toResponse(ticketTag);
        }
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate ticket tag")
    void shouldThrowExceptionWhenCreatingDuplicateTag() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateTicketTagRequest request = new CreateTicketTagRequest(companyId, "Urgent", "#FF0000", "Urgent tickets");

            when(ticketTagRepository.existsByCompanyIdAndName(companyId, "Urgent")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> ticketTagService.create(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Tag com o mesmo nome já existe");

            verify(ticketTagRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should update ticket tag successfully")
    void shouldUpdateTicketTag() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateTicketTagRequest request = new UpdateTicketTagRequest("Very Urgent", "#FF00FF", "Very urgent tickets");

            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(ticketTag));
            when(ticketTagRepository.existsByCompanyIdAndName(companyId, "Very Urgent")).thenReturn(false);
            doNothing().when(mapper).updateEntity(request, ticketTag);
            when(ticketTagRepository.save(ticketTag)).thenReturn(ticketTag);
            when(mapper.toResponse(ticketTag)).thenReturn(ticketTagResponse);

            // Act
            TicketTagResponse result = ticketTagService.update(1, request);

            // Assert
            assertThat(result).isEqualTo(ticketTagResponse);
            verify(ticketTagRepository).findById(1);
            verify(mapper).updateEntity(request, ticketTag);
            verify(ticketTagRepository).save(ticketTag);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate name")
    void shouldThrowExceptionWhenUpdatingToDuplicateName() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateTicketTagRequest request = new UpdateTicketTagRequest("Important", "#0000FF", "Important tickets");

            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(ticketTag));
            when(ticketTagRepository.existsByCompanyIdAndName(companyId, "Important")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> ticketTagService.update(1, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Tag com o mesmo nome já existe");

            verify(ticketTagRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should delete ticket tag successfully")
    void shouldDeleteTicketTag() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(ticketTag));
            doNothing().when(ticketTagRepository).deleteById(1);

            // Act
            ticketTagService.delete(1);

            // Assert
            verify(ticketTagRepository).findById(1);
            verify(ticketTagRepository).deleteById(1);
        }
    }

    @Test
    @DisplayName("Should throw exception when deleting ticket tag from different company")
    void shouldThrowExceptionWhenDeletingTagFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(UUID.randomUUID());
            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(ticketTag));

            // Act & Assert
            assertThatThrownBy(() -> ticketTagService.delete(1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Tag não pertence à empresa atual");

            verify(ticketTagRepository, never()).deleteById(any());
        }
    }
}
