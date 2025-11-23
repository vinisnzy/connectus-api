package com.vinisnzy.connectus_api.domain.automation.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateQuickReplyRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.QuickReplyResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.QuickReply;
import com.vinisnzy.connectus_api.domain.automation.mapper.QuickReplyMapper;
import com.vinisnzy.connectus_api.domain.automation.repository.QuickReplyRepository;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuickReplyService Unit Tests")
class QuickReplyServiceTest {

    @Mock
    private QuickReplyRepository quickReplyRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private QuickReplyMapper mapper;

    @InjectMocks
    private QuickReplyService quickReplyService;

    private UUID companyId;
    private UUID quickReplyId;
    private Company company;
    private QuickReply quickReply;
    private QuickReplyResponse quickReplyResponse;
    private UpdateQuickReplyRequest updateQuickReplyRequest;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        quickReplyId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        quickReply = new QuickReply();
        quickReply.setId(quickReplyId);
        quickReply.setCompany(company);
        quickReply.setShortcut("/hello");
        quickReply.setMessage("Hello! How can I help you?");

        quickReplyResponse = QuickReplyResponse.builder()
                .id(quickReplyId)
                .shortcut("/hello")
                .messageContent("Hello! How can I help you?")
                .build();
        updateQuickReplyRequest = new UpdateQuickReplyRequest(
                quickReplyId,
                "/hello",
                "Hello! How can I help you?",
                "Hello, my name is Bot.",
                ""
        );
    }

    @Test
    @DisplayName("Should find all quick replies")
    void shouldFindAllQuickReplies() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findByCompanyIdAndIsActiveTrue(companyId)).thenReturn(List.of(quickReply));
            when(mapper.toResponse(quickReply)).thenReturn(quickReplyResponse);

            List<QuickReplyResponse> result = quickReplyService.getAll();

            assertThat(result).hasSize(1);
            verify(quickReplyRepository).findByCompanyIdAndIsActiveTrue(companyId);
        }
    }

    @Test
    @DisplayName("Should find all quick replies with pagination")
    void shouldFindAllQuickRepliesWithPagination() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            Pageable pageable = PageRequest.of(0, 10);

            when(quickReplyRepository.findByCompanyIdAndIsActiveTrue(companyId, pageable))
                    .thenReturn(new PageImpl<>(List.of(quickReply), pageable, 1));
            when(mapper.toResponse(quickReply)).thenReturn(quickReplyResponse);

            List<QuickReplyResponse> result = quickReplyService.getAll(pageable);

            assertThat(result).hasSize(1);
            verify(quickReplyRepository).findByCompanyIdAndIsActiveTrue(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should find quick reply by id")
    void shouldFindQuickReplyById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findById(quickReplyId)).thenReturn(Optional.of(quickReply));
            when(mapper.toResponse(quickReply)).thenReturn(quickReplyResponse);

            QuickReplyResponse result = quickReplyService.getById(quickReplyId);

            assertThat(result).isEqualTo(quickReplyResponse);
        }
    }

    @Test
    @DisplayName("Should throw exception when quick reply not found")
    void shouldThrowExceptionWhenQuickReplyNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findById(quickReplyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> quickReplyService.getById(quickReplyId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should return empty list when no quick replies found")
    void shouldReturnEmptyListWhenNoQuickRepliesFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findByCompanyIdAndIsActiveTrue(eq(companyId), any()))
                    .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

            List<QuickReplyResponse> result = quickReplyService.getAll(PageRequest.of(0, 10));

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should find quick replies by name")
    void shouldFindQuickRepliesByName() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String title = "hello";
            when(quickReplyRepository.findByCompanyIdAndTitleContainingIgnoreCase(companyId, title))
                    .thenReturn(List.of(quickReply));
            when(mapper.toResponse(quickReply)).thenReturn(quickReplyResponse);

            List<QuickReplyResponse> result = quickReplyService.getByName(title);

            assertThat(result).hasSize(1);
            verify(quickReplyRepository).findByCompanyIdAndTitleContainingIgnoreCase(companyId, title);
        }
    }

    @Test
    @DisplayName("Should return empty list when no quick replies found by name")
    void shouldReturnEmptyListWhenNoQuickRepliesFoundByName() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String title = "nonexistent";
            when(quickReplyRepository.findByCompanyIdAndTitleContainingIgnoreCase(companyId, title))
                    .thenReturn(List.of());

            List<QuickReplyResponse> result = quickReplyService.getByName(title);

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should update quick reply successfully")
    void shouldUpdateQuickReply() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findById(quickReplyId)).thenReturn(Optional.of(quickReply));
            when(quickReplyRepository.save(quickReply)).thenReturn(quickReply);
            when(mapper.toResponse(quickReply)).thenReturn(quickReplyResponse);

            QuickReplyResponse result = quickReplyService.update(updateQuickReplyRequest);

            assertThat(result).isEqualTo(quickReplyResponse);
            verify(quickReplyRepository).save(quickReply);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent quick reply")
    void shouldThrowExceptionWhenUpdatingNonExistentQuickReply() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(quickReplyRepository.findById(quickReplyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> quickReplyService.update(updateQuickReplyRequest))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should delete quick reply successfully")
    void shouldDeleteQuickReply() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            quickReplyService.delete(quickReplyId);

            verify(quickReplyRepository).deleteById(quickReplyId);
        }
    }
}
