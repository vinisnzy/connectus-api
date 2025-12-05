package com.vinisnzy.connectus_api.domain.analytics.service;

import com.vinisnzy.connectus_api.domain.analytics.dto.response.ActivityLogResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.ActivityLog;
import com.vinisnzy.connectus_api.domain.analytics.mapper.ActivityLogMapper;
import com.vinisnzy.connectus_api.domain.analytics.repository.ActivityLogRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;
    private final ActivityLogMapper mapper;

    @Async
    public void log(String action, String entityType, UUID entityId) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        ActivityLog log = new ActivityLog();

        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);

        repository.save(log);
    }

    @Async
    public void log(String action, String entityType, Integer entityId) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        ActivityLog log = new ActivityLog();

        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);

        repository.save(log);
    }

    public List<ActivityLogResponse> getLogsByCompany(UUID companyId, Pageable pageable) {
        Page<ActivityLog> logs = repository.findByCompanyIdOrderByCreatedAt(companyId, pageable);
        return logs.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
