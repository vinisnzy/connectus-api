package com.vinisnzy.connectus_api.domain.messaging.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.api.exception.SendMessageException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.service.ContactService;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.CreateTicketRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.ReceiveMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.SendMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.MessageResponse;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.TicketResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ContactService contactService;
    private final MessageMapper mapper;
    private final N8nClient n8nClient;
    private final TicketService ticketService;

    public List<MessageResponse> findAll(Pageable pageable) {
        Company company = getCurrentCompanyOrThrow();
        Page<Message> messages = messageRepository.findByCompanyOrderBySentAtDesc(company, pageable);
        return messages.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public MessageResponse findById(UUID id) {
        Message message = getMessageOrThrow(id);
        return mapper.toResponse(message);
    }

    public List<MessageResponse> findByTicketId(UUID ticketId, Pageable pageable) {
        Ticket ticket = getTicketOrThrow(ticketId);

        Page<Message> messages = messageRepository.findByTicketOrderBySentAtAsc(ticket, pageable);
        return messages.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Optional<MessageResponse> findByExternalId(String externalId) {
        return messageRepository.findById(UUID.fromString(externalId))
                .map(mapper::toResponse);
    }

    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        Ticket ticket = getTicketOrThrow(request.ticketId());
        Company company = getCurrentCompanyOrThrow();
        User user = userRepository.findById(request.senderUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + request.senderUserId()));

        // POR ENQUANTO é salvo no banco, mas futuramente provavelmente o n8n salvará a mensagem e retornará o id
        Message message = mapper.toEntity(request);
        message.setCompany(company);
        message.setTicket(ticket);
        message.setSentByUser(user);
        message.setDirection(MessageDirection.OUTBOUND);
        message.setSenderType(SenderType.USER);
        message.setIsRead(true);

        try {
            n8nClient.sendMessage(request);
            message = messageRepository.save(message);

            contactService.updateLastInteraction(ticket.getContact().getId());

            return mapper.toResponse(message);
        } catch (Exception e) {
            throw new SendMessageException("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    @Transactional
    public MessageResponse receiveMessage(ReceiveMessageRequest request) {
        Company company = getCurrentCompanyOrThrow();

        Contact contact = contactService.findOrCreateByPhone(request.receiveFromNumber(), company.getId());

        Ticket ticket = ticketRepository.findFirstByContactIdAndStatusOpenOrPending(contact.getId())
                .orElseGet(() -> {
                    CreateTicketRequest ticketRequest = new CreateTicketRequest(contact.getId(), null, null);
                    TicketResponse ticketResponse = ticketService.create(ticketRequest);
                    return getTicketOrThrow(ticketResponse.id());
                });

        Message message = new Message();
        message.setTicket(ticket);
        message.setCompany(company);
        message.setDirection(MessageDirection.INBOUND);
        message.setMessageType(MessageType.valueOf(request.messageType().toUpperCase()));
        message.setSenderType(SenderType.CONTACT);
        message.setContent(request.content());
        message.setIsRead(false);
        message.setSentAt(request.sentAt().atZone(ZonedDateTime.now().getZone()));

        message = messageRepository.save(message);

        contactService.updateLastInteraction(contact.getId());
        markAsDelivered(message.getId());

        return mapper.toResponse(message);
    }

    public void markAsRead(UUID id) {
        Message message = getMessageOrThrow(id);

        if (message.getDirection() == MessageDirection.INBOUND && !message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(ZonedDateTime.now());
            messageRepository.save(message);
        }
    }

    @Transactional
    public void markMultipleAsRead(List<UUID> messageIds) {
        for (UUID messageId : messageIds) {
            this.markAsRead(messageId);
        }
    }

    public void markAsDelivered(UUID id) {
        Message message = getMessageOrThrow(id);

        if (message.getDirection() == MessageDirection.OUTBOUND && message.getDeliveredAt() == null) {
            message.setDeliveredAt(ZonedDateTime.now());
            messageRepository.save(message);
        }
    }

    public long countUnreadByTicketId(UUID ticketId) {
        Ticket ticket = getTicketOrThrow(ticketId);

        return messageRepository.countUnreadByTicketAndDirection(ticket, MessageDirection.INBOUND);
    }

    public long countUnreadByCompany() {
        Company company = getCurrentCompanyOrThrow();
        return messageRepository.countByCompanyIdAndIsReadFalseAndDirection(company.getId(), MessageDirection.INBOUND);
    }

    public List<MessageResponse> findByDirection(MessageDirection direction, Pageable pageable) {
        Company company = getCurrentCompanyOrThrow();

        Page<Message> messages = messageRepository.findByCompanyOrderBySentAtDesc(company, pageable);
        return messages.getContent()
                .stream()
                .filter(message -> message.getDirection() == direction)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        Message message = getMessageOrThrow(id);
        // TODO: Adicionar e utilizar método do N8nClient para apaagar a mensagem no WhatsApp também
        messageRepository.deleteById(message.getId());
    }

    public List<MessageResponse> getLastMessages(UUID ticketId, int limit) {
        Ticket ticket = getTicketOrThrow(ticketId);

        List<Message> messages = messageRepository.findTop10ByTicketOrderBySentAtDesc(ticket);
        return messages.stream()
                .limit(limit)
                .map(mapper::toResponse)
                .toList();
    }

    private Ticket getTicketOrThrow(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket não encontrado com o id: " + id));
        validateTicketBelongsToCompany(ticket);
        return ticket;
    }

    private Message getMessageOrThrow(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensagem não encontrada com o id: " + id));
        validateMessageBelongsToCompany(message);
        return message;
    }

    private Company getCurrentCompanyOrThrow() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));
    }

    private void validateMessageBelongsToCompany(Message message) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!message.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Mensagem não pertence à empresa atual.");
        }
    }

    private void validateTicketBelongsToCompany(Ticket ticket) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!ticket.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Ticket não pertence à empresa atual.");
        }
    }
}
