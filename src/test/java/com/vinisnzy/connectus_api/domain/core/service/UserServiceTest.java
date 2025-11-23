package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.mapper.UserMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.infra.utils.PasswordUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordUtils passwordUtils;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    private UUID companyId;
    private UUID userId;
    private Company company;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        user = new User();
        user.setId(userId);
        user.setCompany(company);
        user.setEmail("test@example.com");

        userResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("Should find all users for company")
    void shouldFindAllUsers() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Page<User> userPage = new PageImpl<>(List.of(user));

            when(userRepository.findByCompanyId(eq(companyId), any())).thenReturn(userPage);
            when(mapper.toResponse(user)).thenReturn(userResponse);

            List<UserResponse> result = userService.findAll(PageRequest.of(0, 10));

            assertThat(result).hasSize(1);
            verify(userRepository).findByCompanyId(eq(companyId), any());
        }
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(mapper.toResponse(user)).thenReturn(userResponse);

            UserResponse result = userService.findById(userId);

            assertThat(result).isEqualTo(userResponse);
        }
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findById(userId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
