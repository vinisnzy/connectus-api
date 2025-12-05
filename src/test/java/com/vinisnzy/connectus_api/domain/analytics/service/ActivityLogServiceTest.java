package com.vinisnzy.connectus_api.domain.analytics.service;

import com.vinisnzy.connectus_api.domain.analytics.dto.response.ActivityLogResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.ActivityLog;
import com.vinisnzy.connectus_api.domain.analytics.mapper.ActivityLogMapper;
import com.vinisnzy.connectus_api.domain.analytics.repository.ActivityLogRepository;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityLogService Unit Tests")
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;
    @Mock
    private ActivityLogMapper mapper;

    @InjectMocks
    private ActivityLogService activityLogService;

    private UUID companyId;
    private UUID userId;
    private Company company;
    private User user;
    private ActivityLog activityLog;
    private ActivityLogResponse activityLogResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        user = new User();
        user.setId(userId);
        user.setCompany(company);

        activityLog = new ActivityLog();
        activityLog.setId(UUID.randomUUID());
        activityLog.setCompanyId(company.getId());
        activityLog.setUserId(user.getId());
        activityLog.setAction("ticket.created");
        activityLog.setEntityType("ticket");

        activityLogResponse = ActivityLogResponse.builder()
                .id(activityLog.getId())
                .action("ticket.created")
                .build();
    }

    @Test
    @DisplayName("Should find all activity logs for company")
    void shouldFindAllActivityLogsForCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Page<ActivityLog> logPage = new PageImpl<>(List.of(activityLog));
            Pageable pageable = PageRequest.of(0, 10);

            when(activityLogRepository.findByCompanyIdOrderByCreatedAt(eq(companyId), any())).thenReturn(logPage);
            when(mapper.toResponse(activityLog)).thenReturn(activityLogResponse);
            List<ActivityLogResponse> result = activityLogService.getLogsByCompany(companyId, pageable);

            assertThat(result).hasSize(1);
            verify(activityLogRepository).findByCompanyIdOrderByCreatedAt(eq(companyId), any());
        }
    }

    @Test
    @DisplayName("Should log activity")
    void shouldLogActivity() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);

            when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(activityLog);

            activityLogService.log("ticket.created", "ticket", UUID.randomUUID());

            verify(activityLogRepository).save(any(ActivityLog.class));
        }
    }

    @Test
    @DisplayName("Should return empty list when no activity logs found")
    void shouldReturnEmptyListWhenNoActivityLogsFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Pageable pageable = PageRequest.of(0, 10);
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(activityLogRepository.findByCompanyIdOrderByCreatedAt(eq(companyId), any())).thenReturn(new PageImpl<>(List.of()));

            List<ActivityLogResponse> result = activityLogService.getLogsByCompany(companyId, pageable);

            assertThat(result).isEmpty();
        }
    }
}
