package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.dto.response.SubscriptionResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Subscription;
import com.vinisnzy.connectus_api.domain.core.mapper.SubscriptionMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.PlanRepository;
import com.vinisnzy.connectus_api.domain.core.repository.SubscriptionRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Unit Tests")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private UUID companyId;
    private UUID subscriptionId;
    private Company company;
    private Subscription subscription;
    private SubscriptionResponse subscriptionResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        subscriptionId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setCompany(company);

        subscriptionResponse = SubscriptionResponse.builder()
                .id(subscriptionId)
                .build();
    }

    @Test
    @DisplayName("Should find subscription by company id")
    void shouldFindSubscriptionByCompanyId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(subscriptionRepository.findByCompanyId(companyId)).thenReturn(Optional.of(subscription));
            when(mapper.toResponse(subscription)).thenReturn(subscriptionResponse);

            SubscriptionResponse result = subscriptionService.findCurrentCompanySubscription();

            assertThat(result).isEqualTo(subscriptionResponse);
            verify(subscriptionRepository).findByCompanyId(companyId);
        }
    }

    @Test
    @DisplayName("Should throw exception when subscription not found")
    void shouldThrowExceptionWhenSubscriptionNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(subscriptionRepository.findByCompanyId(companyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subscriptionService.findCurrentCompanySubscription())
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
