package com.vinisnzy.connectus_api.api.controller.analytics;

import com.vinisnzy.connectus_api.domain.analytics.dto.response.ActivityLogResponse;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService service;

    @GetMapping("/company/{companyId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'logs', 'view')")
    public ResponseEntity<List<ActivityLogResponse>> getLogsByCompany(
            @PathVariable UUID companyId,
            Pageable pageable) {
        List<ActivityLogResponse> logs = service.getLogsByCompany(companyId, pageable);
        return ResponseEntity.ok(logs);
    }
}
