package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.dto.response.PlanResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Plan;
import com.vinisnzy.connectus_api.domain.core.mapper.PlanMapper;
import com.vinisnzy.connectus_api.domain.core.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlanService Unit Tests")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;
    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private PlanService planService;

    private UUID planId;
    private Plan plan;
    private PlanResponse planResponse;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();

        plan = new Plan();
        plan.setId(planId);
        plan.setName("Basic");

        planResponse = PlanResponse.builder()
                .id(planId)
                .name("Basic")
                .build();
    }

    @Test
    @DisplayName("Should find all active plans")
    void shouldFindAllActivePlans() {
        when(planRepository.findByIsActiveTrueOrderByYearlyPriceDesc()).thenReturn(List.of(plan));
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        List<PlanResponse> result = planService.findAllActive();

        assertThat(result).hasSize(1);
        verify(planRepository).findByIsActiveTrueOrderByYearlyPriceDesc();
    }

    @Test
    @DisplayName("Should find plan by id")
    void shouldFindPlanById() {
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(mapper.toResponse(plan)).thenReturn(planResponse);

        PlanResponse result = planService.findById(planId);

        assertThat(result).isEqualTo(planResponse);
    }

    @Test
    @DisplayName("Should throw exception when plan not found")
    void shouldThrowExceptionWhenPlanNotFound() {
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planService.findById(planId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
