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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTagService {

    private final TicketTagRepository ticketTagRepository;
    private final CompanyRepository companyRepository;
    private final TicketTagMapper mapper;

    public List<TicketTagResponse> findAll() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<TicketTag> tags = ticketTagRepository.findByCompanyIdOrderByNameAsc(companyId);
        return tags.stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Paginated
    public List<TicketTagResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return ticketTagRepository.findByCompanyIdOrderByNameAsc(companyId, pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public TicketTagResponse findById(Integer id) {
        TicketTag tag = getTicketTagOrThrow(id);
        return mapper.toResponse(tag);
    }

    public TicketTagResponse findByName(String name) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        TicketTag tag = ticketTagRepository.findByCompanyIdAndName(companyId, name)
                .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com o nome: " + name));
        return mapper.toResponse(tag);
    }

    @Transactional
    public TicketTagResponse create(CreateTicketTagRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        if (ticketTagRepository.existsByCompanyIdAndName(companyId, request.name())) {
            throw new IllegalStateException("Tag com o mesmo nome já existe.");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        TicketTag ticketTag = mapper.toEntity(request);
        ticketTag.setCompany(company);

        ticketTag = ticketTagRepository.save(ticketTag);
        return mapper.toResponse(ticketTag);
    }

    @Transactional
    public TicketTagResponse update(Integer id, UpdateTicketTagRequest request) {
        TicketTag ticketTag = getTicketTagOrThrow(id);

        if (request.name() != null && !request.name().equals(ticketTag.getName())) {
            UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
            if (ticketTagRepository.existsByCompanyIdAndName(companyId, request.name())) {
                throw new IllegalStateException("Tag com o mesmo nome já existe.");
            }
        }

        mapper.updateEntity(request, ticketTag);
        ticketTag = ticketTagRepository.save(ticketTag);
        return mapper.toResponse(ticketTag);
    }

    @Transactional
    public void delete(Integer id) {
        TicketTag ticketTag = getTicketTagOrThrow(id);
        ticketTagRepository.deleteById(ticketTag.getId());
    }

    private TicketTag getTicketTagOrThrow(Integer id) {
        TicketTag tag = ticketTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com o id: " + id));
        validateTagBelongsToCompany(tag);
        return tag;
    }

    private void validateTagBelongsToCompany(TicketTag tag) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!tag.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Tag não pertence à empresa atual.");
        }
    }
}
