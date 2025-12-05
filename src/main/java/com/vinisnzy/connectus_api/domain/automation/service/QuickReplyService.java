package com.vinisnzy.connectus_api.domain.automation.service;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.QuickReplyResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.QuickReply;
import com.vinisnzy.connectus_api.domain.automation.mapper.QuickReplyMapper;
import com.vinisnzy.connectus_api.domain.automation.repository.QuickReplyRepository;
import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.service.CompanyService;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuickReplyService {

    private final QuickReplyRepository repository;
    private final ActivityLogService activityLogService;
    private final CompanyService companyService;
    private final QuickReplyMapper mapper;

    public QuickReplyResponse create(CreateQuickReplyRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Company company = companyService.findEntityById(companyId);
        QuickReply quickReply = mapper.toEntity(request);
        quickReply.setCompany(company);
        repository.save(quickReply);

        activityLogService.log("ENTITY_CREATED", "QuickReply", quickReply.getId());

        return mapper.toResponse(quickReply);
    }

    public List<QuickReplyResponse> getAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return repository.findByCompanyIdAndIsActiveTrue(companyId, pageable)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<QuickReplyResponse> getAll() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return repository.findByCompanyIdAndIsActiveTrue(companyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public QuickReplyResponse getById(UUID id) {
        QuickReply quickReply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quick Reply não encontrado com id: " + id));
        return mapper.toResponse(quickReply);
    }

    public List<QuickReplyResponse> getByName(String title) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return repository.findByCompanyIdAndTitleContainingIgnoreCase(companyId, title)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public QuickReplyResponse update(UpdateQuickReplyRequest request) {
        QuickReply quickReply = repository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Quick Reply não encontrado com id: " + request.id()));
        mapper.updateEntity(request, quickReply);
        repository.save(quickReply);

        activityLogService.log("ENTITY_UPDATED", "QuickReply", quickReply.getId());

        return mapper.toResponse(quickReply);
    }

    public void delete(UUID id) {
        activityLogService.log("ENTITY_DELETED", "QuickReply", id);

        repository.deleteById(id);
    }
}
