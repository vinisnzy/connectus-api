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
import com.vinisnzy.connectus_api.domain.messaging.entity.TicketTag;
import com.vinisnzy.connectus_api.domain.messaging.entity.Ticket;
import com.vinisnzy.connectus_api.domain.messaging.entity.enums.TicketStatus;
import com.vinisnzy.connectus_api.domain.messaging.mapper.TicketMapper;
import com.vinisnzy.connectus_api.domain.messaging.repository.TicketTagRepository;
import com.vinisnzy.connectus_api.domain.messaging.repository.TicketRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final TicketTagRepository ticketTagRepository;
    private final TicketMapper mapper;

    public List<TicketResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Ticket> tickets = ticketRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return tickets.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public TicketResponse findById(UUID id) {
        Ticket ticket = getTicketOrThrow(id);
        return mapper.toResponse(ticket);
    }

    public List<TicketResponse> findByContactId(UUID contactId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findByContactIdOrderByCreatedAtDesc(contactId, pageable);
        return tickets.getContent()
                .stream()
                .filter(ticket -> ticket.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<TicketResponse> findByAssignedUserId(UUID userId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findByAssignedUserIdOrderByCreatedAtDesc(userId, pageable);
        return tickets.getContent()
                .stream()
                .filter(ticket -> ticket.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<TicketResponse> findByStatus(TicketStatus status, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Ticket> tickets = ticketRepository.findByCompanyIdAndStatusOrderByPriorityDescCreatedAtDesc(companyId, status, pageable);
        return tickets.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        Contact contact = contactRepository.findById(request.contactId())
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com o id: " + request.contactId()));

        if (!contact.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Contato não pertence à empresa atual.");
        }

        Ticket ticket = new Ticket();
        ticket.setCompany(company);
        ticket.setContact(contact);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(0);
        ticket.setChannel("whatsapp");
        ticket.setIsArchived(false);

        Integer lastTicketNumber = ticketRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, Pageable.unpaged())
                .stream()
                .findFirst()
                .map(Ticket::getTicketNumber)
                .orElse(0);
        ticket.setTicketNumber(lastTicketNumber + 1);

        if (request.assignedUserId() != null) {
            User user = userRepository.findById(request.assignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + request.assignedUserId()));
            ticket.setAssignedUser(user);
        }

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse update(UUID id, UpdateTicketRequest request) {
        Ticket ticket = getTicketOrThrow(id);
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        if (request.category() != null) {
            ticket.setCategory(request.category());
        }
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            Set<TicketTag> tags = new HashSet<>();
            for (Integer tagId : request.tagIds()) {
                TicketTag tag = ticketTagRepository.findById(tagId)
                        .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com o id: " + tagId));
                if (!tag.getCompany().getId().equals(companyId)) {
                    throw new IllegalStateException("Tag não pertence à empresa atual.");
                }
                tags.add(tag);
            }
            ticket.setTags(tags);
        }

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse assign(UUID id, UUID userId) {
        Ticket ticket = getTicketOrThrow(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + userId));

        if (!user.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow())) {
            throw new IllegalStateException("Usuário não pertence à empresa atual.");
        }

        ticket.setAssignedUser(user);
        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse unassign(UUID id) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setAssignedUser(null);
        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(UUID id, TicketStatus newStatus) {
        Ticket ticket = getTicketOrThrow(id);

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED && oldStatus != TicketStatus.RESOLVED) {
            ticket.setResolvedAt(ZonedDateTime.now());
        } else if (newStatus == TicketStatus.CLOSED && oldStatus != TicketStatus.CLOSED) {
            ticket.setClosedAt(ZonedDateTime.now());
        } else if (newStatus == TicketStatus.IN_PROGRESS && ticket.getFirstResponseAt() == null) {
            ticket.setFirstResponseAt(ZonedDateTime.now());
        }

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse resolve(UUID id, ResolveTicketRequest request) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolutionType(request.resolutionType());
        ticket.setResolutionNotes(request.resolutionNotes());
        ticket.setResolvedAt(ZonedDateTime.now());

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse close(UUID id) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(ZonedDateTime.now());

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse reopen(UUID id) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setResolvedAt(null);
        ticket.setClosedAt(null);
        ticket.setResolutionType(null);
        ticket.setResolutionNotes(null);

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse setPending(UUID id, ZonedDateTime pendingUntil) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setStatus(TicketStatus.PENDING);
        ticket.setPendingUntil(pendingUntil);

        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse archive(UUID id) {
        Ticket ticket = getTicketOrThrow(id);
        if (ticket.getStatus() != TicketStatus.CLOSED) {
            throw new IllegalStateException("Apenas tickets fechados podem ser arquivados.");
        }

        ticket.setIsArchived(true);
        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse unarchive(UUID id) {
        Ticket ticket = getTicketOrThrow(id);

        ticket.setIsArchived(false);
        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse addTags(UUID id, AddTagsToTicketRequest request) {
        Ticket ticket = getTicketOrThrow(id);
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        if (request.tagIds() == null || request.tagIds().isEmpty()) {
            throw new IllegalArgumentException("Lista de IDs de tags não pode ser vazia.");
        }

        Set<TicketTag> existingTags = ticket.getTags() != null ? ticket.getTags() : new HashSet<>();

        for (Integer tagId : request.tagIds()) {
            TicketTag tag = ticketTagRepository.findById(tagId)
                    .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com o id: " + tagId));
            if (!tag.getCompany().getId().equals(companyId)) {
                throw new IllegalStateException("Tag não pertence à empresa atual.");
            }
            existingTags.add(tag);
        }

        ticket.setTags(existingTags);
        ticket = ticketRepository.save(ticket);
        return mapper.toResponse(ticket);
    }

    public List<TicketResponse> findOverdue() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<Ticket> overdueTickets = ticketRepository.findOverdueBySlaDeadline(companyId, ZonedDateTime.now());
        return overdueTickets.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<TicketResponse> findUnassigned(TicketStatus status, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Ticket> tickets = ticketRepository.findUnassignedByCompanyIdAndStatus(companyId, status, pageable);
        return tickets.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Long countByStatus(TicketStatus status) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return ticketRepository.countByCompanyIdAndStatus(companyId, status);
    }

    public List<TicketResponse> findNonArchived(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Ticket> tickets = ticketRepository.findByIsArchivedFalseAndCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return tickets.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Ticket getTicketOrThrow(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket não encontrado com o id: " + id));
        validateTicketBelongsToCompany(ticket);
        return ticket;
    }

    private void validateTicketBelongsToCompany(Ticket ticket) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!ticket.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Ticket não pertence à empresa atual.");
        }
    }
}
