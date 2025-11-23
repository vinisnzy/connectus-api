package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.domain.core.dto.response.RoleResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.mapper.RoleMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RoleMapper mapper;

    @InjectMocks
    private RoleService roleService;

    private UUID companyId;
    private Company company;
    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        company = new Company();
        company.setId(companyId);

        role = new Role();
        role.setId(1);
        role.setCompany(company);
        role.setName("Admin");

        roleResponse = RoleResponse.builder()
                .id(1)
                .name("Admin")
                .build();
    }

    @Test
    @DisplayName("Should find all roles for company")
    void shouldFindAllRoles() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(roleRepository.findByCompanyId(companyId)).thenReturn(List.of(role));
            when(mapper.toResponse(role)).thenReturn(roleResponse);

            List<RoleResponse> result = roleService.findAll();

            assertThat(result).hasSize(1);
            verify(roleRepository).findByCompanyId(companyId);
        }
    }

    @Test
    @DisplayName("Should find role by id")
    void shouldFindRoleById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(roleRepository.findById(1)).thenReturn(Optional.of(role));
            when(mapper.toResponse(role)).thenReturn(roleResponse);

            RoleResponse result = roleService.findById(1);

            assertThat(result).isEqualTo(roleResponse);
        }
    }
}
