package com.vinisnzy.connectus_api.domain.messaging.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.AddTagsToTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.ResolveTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.UpdateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketResponse;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.TicketTag;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.ResolutionType;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.TicketStatus;
import com.vinisnzy.connectus_api.domain.messaging.mapper.TicketMapper;
import com.vinisnzy.connectus_api.domain.messaging.repository.TicketRepository;
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

import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService Unit Tests")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketTagRepository ticketTagRepository;

    @Mock
    private TicketMapper mapper;

    @InjectMocks
    private TicketService ticketService;

    private UUID companyId;
    private UUID ticketId;
    private UUID contactId;
    private UUID userId;
    private Company company;
    private Contact contact;
    private User user;
    private Ticket ticket;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        contactId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);
        company.setName("Test Company");

        contact = new Contact();
        contact.setId(contactId);
        contact.setCompany(company);
        contact.setName("Test Contact");

        user = new User();
        user.setId(userId);
        user.setCompany(company);
        user.setName("Test User");

        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setCompany(company);
        ticket.setContact(contact);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setTicketNumber(1);

        ticketResponse = TicketResponse.builder()
                .id(ticketId)
                .status("OPEN")
                .build();
    }

    @Test
    @DisplayName("Should find all tickets for company")
    void shouldFindAllTickets() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));

            when(ticketRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable))
                    .thenReturn(ticketPage);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            List<TicketResponse> result = ticketService.findAll(pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(ticketResponse);
            verify(ticketRepository).findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
            verify(mapper).toResponse(ticket);
        }
    }

    @Test
    @DisplayName("Should find ticket by id")
    void shouldFindTicketById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.findById(ticketId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            verify(ticketRepository).findById(ticketId);
            verify(mapper).toResponse(ticket);
        }
    }

    @Test
    @DisplayName("Should throw exception when ticket not found")
    void shouldThrowExceptionWhenTicketNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> ticketService.findById(ticketId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Ticket não encontrado");

            verify(ticketRepository).findById(ticketId);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Test
    @DisplayName("Should create ticket successfully")
    void shouldCreateTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateTicketRequest request = new CreateTicketRequest(contactId, userId, null);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketRepository.findByCompanyIdOrderByCreatedAtDesc(eq(companyId), any(Pageable.class)))
                    .thenReturn(Page.empty());
            when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.create(request);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            verify(companyRepository).findById(companyId);
            verify(contactRepository).findById(contactId);
            verify(userRepository).findById(userId);
            verify(ticketRepository).save(any(Ticket.class));
            verify(mapper).toResponse(ticket);
        }
    }

    @Test
    @DisplayName("Should throw exception when creating ticket with invalid contact")
    void shouldThrowExceptionWhenContactNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateTicketRequest request = new CreateTicketRequest(contactId, null, null);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> ticketService.create(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Contato não encontrado");

            verify(contactRepository).findById(contactId);
            verify(ticketRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should throw exception when contact belongs to different company")
    void shouldThrowExceptionWhenContactBelongsToDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateTicketRequest request = new CreateTicketRequest(contactId, null, null);

            Company differentCompany = new Company();
            differentCompany.setId(UUID.randomUUID());
            contact.setCompany(differentCompany);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

            // Act & Assert
            assertThatThrownBy(() -> ticketService.create(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Contato não pertence à empresa atual");

            verify(ticketRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should update ticket successfully")
    void shouldUpdateTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateTicketRequest request = new UpdateTicketRequest(1, "Category", null);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.update(ticketId, request);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getPriority()).isEqualTo(1);
            assertThat(ticket.getCategory()).isEqualTo("Category");
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should assign user to ticket")
    void shouldAssignUserToTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.assign(ticketId, userId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getAssignedUser()).isEqualTo(user);
            verify(userRepository).findById(userId);
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should throw exception when assigning user from different company")
    void shouldThrowExceptionWhenAssigningUserFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Company differentCompany = new Company();
            differentCompany.setId(UUID.randomUUID());
            user.setCompany(differentCompany);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // Act & Assert
            assertThatThrownBy(() -> ticketService.assign(ticketId, userId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Usuário não pertence à empresa atual");

            verify(ticketRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should unassign user from ticket")
    void shouldUnassignUserFromTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ticket.setAssignedUser(user);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.unassign(ticketId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getAssignedUser()).isNull();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should update ticket status")
    void shouldUpdateTicketStatus() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.updateStatus(ticketId, TicketStatus.IN_PROGRESS);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
            assertThat(ticket.getFirstResponseAt()).isNotNull();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should resolve ticket")
    void shouldResolveTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ResolveTicketRequest request = new ResolveTicketRequest(ResolutionType.SALE, "Resolved notes");

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.resolve(ticketId, request);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESOLVED);
            assertThat(ticket.getResolutionType()).isEqualTo(ResolutionType.SALE);
            assertThat(ticket.getResolutionNotes()).isEqualTo("Resolved notes");
            assertThat(ticket.getResolvedAt()).isNotNull();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should close ticket")
    void shouldCloseTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.close(ticketId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSED);
            assertThat(ticket.getClosedAt()).isNotNull();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should reopen ticket")
    void shouldReopenTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ticket.setStatus(TicketStatus.CLOSED);
            ticket.setResolvedAt(ZonedDateTime.now());
            ticket.setClosedAt(ZonedDateTime.now());

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.reopen(ticketId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
            assertThat(ticket.getResolvedAt()).isNull();
            assertThat(ticket.getClosedAt()).isNull();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should set ticket as pending")
    void shouldSetTicketAsPending() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ZonedDateTime pendingUntil = ZonedDateTime.now().plusDays(1);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.setPending(ticketId, pendingUntil);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.PENDING);
            assertThat(ticket.getPendingUntil()).isEqualTo(pendingUntil);
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should archive ticket when status is closed")
    void shouldArchiveTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ticket.setStatus(TicketStatus.CLOSED);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.archive(ticketId);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getIsArchived()).isTrue();
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should throw exception when archiving non-closed ticket")
    void shouldThrowExceptionWhenArchivingNonClosedTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ticket.setStatus(TicketStatus.OPEN);

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

            // Act & Assert
            assertThatThrownBy(() -> ticketService.archive(ticketId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Apenas tickets fechados podem ser arquivados");

            verify(ticketRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should add tags to ticket")
    void shouldAddTagsToTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            TicketTag tag1 = new TicketTag();
            tag1.setId(1);
            tag1.setName("Tag 1");
            tag1.setCompany(company);

            TicketTag tag2 = new TicketTag();
            tag2.setId(2);
            tag2.setName("Tag 2");
            tag2.setCompany(company);

            AddTagsToTicketRequest request = new AddTagsToTicketRequest(List.of(1, 2));

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(ticketTagRepository.findById(1)).thenReturn(Optional.of(tag1));
            when(ticketTagRepository.findById(2)).thenReturn(Optional.of(tag2));
            when(ticketRepository.save(ticket)).thenReturn(ticket);
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            TicketResponse result = ticketService.addTags(ticketId, request);

            // Assert
            assertThat(result).isEqualTo(ticketResponse);
            assertThat(ticket.getTags()).hasSize(2);
            assertThat(ticket.getTags()).containsExactlyInAnyOrder(tag1, tag2);
            verify(ticketRepository).save(ticket);
        }
    }

    @Test
    @DisplayName("Should find overdue tickets")
    void shouldFindOverdueTickets() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findOverdueBySlaDeadline(eq(companyId), any(ZonedDateTime.class)))
                    .thenReturn(List.of(ticket));
            when(mapper.toResponse(ticket)).thenReturn(ticketResponse);

            // Act
            List<TicketResponse> result = ticketService.findOverdue();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(ticketResponse);
            verify(ticketRepository).findOverdueBySlaDeadline(eq(companyId), any(ZonedDateTime.class));
        }
    }

    @Test
    @DisplayName("Should count tickets by status")
    void shouldCountTicketsByStatus() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.countByCompanyIdAndStatus(companyId, TicketStatus.OPEN))
                    .thenReturn(5L);

            // Act
            Long count = ticketService.countByStatus(TicketStatus.OPEN);

            // Assert
            assertThat(count).isEqualTo(5L);
            verify(ticketRepository).countByCompanyIdAndStatus(companyId, TicketStatus.OPEN);
        }
    }
}
