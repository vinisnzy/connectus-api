package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.dto.response.CompanyResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.mapper.CompanyMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService Unit Tests")
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CompanyMapper mapper;

    @InjectMocks
    private CompanyService companyService;

    private UUID companyId;
    private Company company;
    private CompanyResponse companyResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        company = new Company();
        company.setId(companyId);
        company.setName("Test Company");

        companyResponse = CompanyResponse.builder()
                .id(companyId)
                .name("Test Company")
                .build();
    }

    @Test
    @DisplayName("Should find company by id")
    void shouldFindCompanyById() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(mapper.toResponse(company)).thenReturn(companyResponse);

        CompanyResponse result = companyService.findById(companyId);

        assertThat(result).isEqualTo(companyResponse);
        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("Should throw exception when company not found")
    void shouldThrowExceptionWhenCompanyNotFound() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findById(companyId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
