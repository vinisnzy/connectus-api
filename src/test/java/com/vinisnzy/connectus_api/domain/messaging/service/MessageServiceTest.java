package com.vinisnzy.connectus_api.domain.messaging.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.crm.service.ContactService;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.MessageResponse;
import com.vinisnzy.connectus_api.domain.messaging.entity.Message;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageDirection;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.MessageType;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.SenderType;
import com.vinisnzy.connectus_api.domain.messaging.mapper.MessageMapper;
import com.vinisnzy.connectus_api.domain.messaging.repository.MessageRepository;
import com.vinisnzy.connectus_api.domain.messaging.repository.TicketRepository;
import com.vinisnzy.connectus_api.infra.client.N8nClient;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService Unit Tests")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactService contactService;

    @Mock
    private MessageMapper mapper;

    @Mock
    private N8nClient n8nClient;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private MessageService messageService;

    private UUID companyId;
    private UUID ticketId;
    private UUID messageId;
    private UUID userId;
    private Company company;
    private Ticket ticket;
    private Message message;
    private MessageResponse messageResponse;
    private User user;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);
        company.setName("Test Company");

        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setCompany(company);

        user = new User();
        user.setId(userId);
        user.setCompany(company);

        message = new Message();
        message.setId(messageId);
        message.setCompany(company);
        message.setTicket(ticket);
        message.setDirection(MessageDirection.INBOUND);
        message.setMessageType(MessageType.TEXT);
        message.setSenderType(SenderType.CONTACT);
        message.setContent(Map.of("text", "Hello"));
        message.setSentAt(ZonedDateTime.now());

        messageResponse = MessageResponse.builder()
                .id(messageId)
                .direction("INBOUND")
                .messageType("TEXT")
                .content(Map.of("text", "Hello"))
                .build();
    }

    @Test
    @DisplayName("Should find all messages for company")
    void shouldFindAllMessages() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(List.of(message));

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(messageRepository.findByCompanyOrderBySentAtDesc(company, pageable)).thenReturn(messagePage);
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            // Act
            List<MessageResponse> result = messageService.findAll(pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(messageResponse);
            verify(messageRepository).findByCompanyOrderBySentAtDesc(company, pageable);
        }
    }

    @Test
    @DisplayName("Should find message by id")
    void shouldFindMessageById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            // Act
            MessageResponse result = messageService.findById(messageId);

            // Assert
            assertThat(result).isEqualTo(messageResponse);
            verify(messageRepository).findById(messageId);
        }
    }

    @Test
    @DisplayName("Should throw exception when message not found")
    void shouldThrowExceptionWhenMessageNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> messageService.findById(messageId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Mensagem não encontrada");

            verify(messageRepository).findById(messageId);
        }
    }

    @Test
    @DisplayName("Should find messages by ticket id")
    void shouldFindMessagesByTicketId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(List.of(message));

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(messageRepository.findByTicketOrderBySentAtAsc(ticket, pageable)).thenReturn(messagePage);
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            // Act
            List<MessageResponse> result = messageService.findByTicketId(ticketId, pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(messageResponse);
            verify(ticketRepository).findById(ticketId);
            verify(messageRepository).findByTicketOrderBySentAtAsc(ticket, pageable);
        }
    }

    @Test
    @DisplayName("Should mark message as read")
    void shouldMarkMessageAsRead() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            message.setIsRead(false);

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(message)).thenReturn(message);

            // Act
            messageService.markAsRead(messageId);

            // Assert
            assertThat(message.getIsRead()).isTrue();
            assertThat(message.getReadAt()).isNotNull();
            verify(messageRepository).save(message);
        }
    }

    @Test
    @DisplayName("Should delete message successfully")
    void shouldDeleteMessage() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            doNothing().when(messageRepository).deleteById(messageId);

            // Act
            messageService.delete(messageId);

            // Assert
            verify(messageRepository).findById(messageId);
            verify(messageRepository).deleteById(messageId);
        }
    }

    @Test
    @DisplayName("Should throw exception when deleting message from different company")
    void shouldThrowExceptionWhenDeletingMessageFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(UUID.randomUUID());
            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

            // Act & Assert
            assertThatThrownBy(() -> messageService.delete(messageId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Mensagem não pertence à empresa atual");

            verify(messageRepository, never()).deleteById(any());
        }
    }

    @Test
    @DisplayName("Should return empty list when no messages found")
    void shouldReturnEmptyListWhenNoMessagesFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(List.of());

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(messageRepository.findByCompanyOrderBySentAtDesc(company, pageable)).thenReturn(messagePage);

            List<MessageResponse> result = messageService.findAll(pageable);

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should find message by external id")
    void shouldFindMessageByExternalId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String externalId = messageId.toString();

            when(messageRepository.findById(UUID.fromString(externalId))).thenReturn(Optional.of(message));
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            Optional<MessageResponse> result = messageService.findByExternalId(externalId);

            MessageResponse resultGet = null;
            if (result.isPresent()) {
                resultGet = result.get();
            }

            assertThat(result).isPresent();
            assertThat(resultGet).isEqualTo(messageResponse);
        }
    }

    @Test
    @DisplayName("Should return empty when external id not found")
    void shouldReturnEmptyWhenExternalIdNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String externalId = UUID.randomUUID().toString();

            when(messageRepository.findById(UUID.fromString(externalId))).thenReturn(Optional.empty());

            Optional<MessageResponse> result = messageService.findByExternalId(externalId);

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should mark multiple messages as read")
    void shouldMarkMultipleMessagesAsRead() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UUID messageId2 = UUID.randomUUID();
            Message message2 = new Message();
            message2.setId(messageId2);
            message2.setCompany(company);
            message2.setDirection(MessageDirection.INBOUND);
            message2.setIsRead(false);
            message.setIsRead(false);

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.findById(messageId2)).thenReturn(Optional.of(message2));

            messageService.markMultipleAsRead(List.of(messageId, messageId2));

            assertThat(message.getIsRead()).isTrue();
            assertThat(message2.getIsRead()).isTrue();
            verify(messageRepository, times(2)).save(any(Message.class));
        }
    }

    @Test
    @DisplayName("Should mark message as delivered")
    void shouldMarkMessageAsDelivered() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            message.setDirection(MessageDirection.OUTBOUND);
            message.setDeliveredAt(null);

            when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
            when(messageRepository.save(message)).thenReturn(message);

            messageService.markAsDelivered(messageId);

            assertThat(message.getDeliveredAt()).isNotNull();
            verify(messageRepository).save(message);
        }
    }

    @Test
    @DisplayName("Should count unread messages by ticket id")
    void shouldCountUnreadMessagesByTicketId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(messageRepository.countUnreadByTicketAndDirection(ticket, MessageDirection.INBOUND)).thenReturn(5L);

            long result = messageService.countUnreadByTicketId(ticketId);

            assertThat(result).isEqualTo(5L);
            verify(messageRepository).countUnreadByTicketAndDirection(ticket, MessageDirection.INBOUND);
        }
    }

    @Test
    @DisplayName("Should return zero when no unread messages by ticket")
    void shouldReturnZeroWhenNoUnreadMessagesByTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(messageRepository.countUnreadByTicketAndDirection(ticket, MessageDirection.INBOUND)).thenReturn(0L);

            long result = messageService.countUnreadByTicketId(ticketId);

            assertThat(result).isZero();
        }
    }

    @Test
    @DisplayName("Should count unread messages by company")
    void shouldCountUnreadMessagesByCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(messageRepository.countByCompanyIdAndIsReadFalseAndDirection(companyId, MessageDirection.INBOUND)).thenReturn(10L);

            long result = messageService.countUnreadByCompany();

            assertThat(result).isEqualTo(10L);
            verify(messageRepository).countByCompanyIdAndIsReadFalseAndDirection(companyId, MessageDirection.INBOUND);
        }
    }

    @Test
    @DisplayName("Should find messages by direction INBOUND")
    void shouldFindMessagesByDirectionInbound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Message> messagePage = new PageImpl<>(List.of(message));
            message.setDirection(MessageDirection.INBOUND);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(messageRepository.findByCompanyOrderBySentAtDesc(company, pageable))
                    .thenReturn(messagePage);
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            List<MessageResponse> result = messageService.findByDirection(MessageDirection.INBOUND, pageable);

            assertThat(result).hasSize(1);
            verify(messageRepository).findByCompanyOrderBySentAtDesc(company, pageable);
        }
    }

    @Test
    @DisplayName("Should find messages by direction OUTBOUND")
    void shouldFindMessagesByDirectionOutbound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            message.setDirection(MessageDirection.OUTBOUND);

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(messageRepository.findByCompanyOrderBySentAtDesc(company, pageable))
                    .thenReturn(new PageImpl<>(List.of(message)));
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            List<MessageResponse> result = messageService.findByDirection(MessageDirection.OUTBOUND, pageable);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    @DisplayName("Should get last messages for ticket")
    void shouldGetLastMessagesForTicket() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            int limit = 5;

            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(messageRepository.findTop10ByTicketOrderBySentAtDesc(ticket))
                    .thenReturn(List.of(message));
            when(mapper.toResponse(message)).thenReturn(messageResponse);

            List<MessageResponse> result = messageService.getLastMessages(ticketId, limit);

            assertThat(result).hasSize(1);
            verify(messageRepository).findTop10ByTicketOrderBySentAtDesc(ticket);
        }
    }

    @Test
    @DisplayName("Should return empty list when no last messages found")
    void shouldReturnEmptyListWhenNoLastMessagesFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
            when(messageRepository.findTop10ByTicketOrderBySentAtDesc(ticket))
                    .thenReturn(List.of());

            List<MessageResponse> result = messageService.getLastMessages(ticketId, 5);

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should throw exception when finding messages by ticket from different company")
    void shouldThrowExceptionWhenFindingMessagesByTicketFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Company differentCompany = new Company();
            Pageable pageable = PageRequest.of(0, 10);
            differentCompany.setId(UUID.randomUUID());
            ticket.setCompany(differentCompany);

            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

            assertThatThrownBy(() -> messageService.findByTicketId(ticketId, pageable))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Ticket não pertence à empresa atual");
        }
    }
}
