package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateUserRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.entity.enums.UserStatus;
import com.vinisnzy.connectus_api.domain.core.mapper.UserMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.core.specifications.UserSpecification;
import com.vinisnzy.connectus_api.infra.utils.PasswordUtils;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import com.vinisnzy.connectus_api.shared.enums.Strength;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtils passwordUtils;
    private final ActivityLogService activityLogService;

    private final UserMapper mapper;

    public List<UserResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<User> users = userRepository.findByCompanyId(companyId, pageable);
        return users.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UserResponse findById(UUID id) {
        User user = getUserByIdOrThrow(id);
        return mapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o email: " + email));
        return mapper.toResponse(user);
    }

    public UserResponse findCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + userId));
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest updatedUser) {
        // TODO: Check permissions for role change
        User user = getUserByIdOrThrow(id);

        if (Boolean.TRUE.equals(user.getIsMaster())) {
            throw new IllegalStateException("Não é possível editar um usuário master");
        }

        if (user.getCompany().getId() == null || !user.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow())) {
            throw new IllegalStateException("Usuário não pertence à empresa atual");
        }

        if (!Objects.equals(user.getEmail(), updatedUser.email())) {
            if (userRepository.existsByEmail(updatedUser.email())) {
                throw new IllegalStateException("Email já está em uso por outro usuário");
            }
        }

        mapper.updateEntity(updatedUser, user);

        user = userRepository.save(user);

        activityLogService.log("ENTITY_UPDATED", "User", user.getId());

        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = getUserByIdOrThrow(id);

        if (Boolean.TRUE.equals(user.getIsMaster())) {
            throw new IllegalStateException("Não é possível editar um usuário master");
        }

        if (user.getCompany().getId() == null || !user.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow())) {
            throw new IllegalStateException("Usuário não pertence à empresa atual");
        }
        // TODO: Reassign user's tickets, contacts, etc. to another user
        // TODO: Implement soft delete or anonymize data based on business rules

        activityLogService.log("ENTITY_DELETED", "User", id);

        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse toggleActive(UUID id, boolean isActive) {
        User user = getUserByIdOrThrow(id);

        validateUserCompany(user);
        validateMasterUser(user, id);
        // TODO: Close active sessions if deactivating
        // TODO: Send notification

        user.setIsActive(isActive);
        user = userRepository.save(user);

        activityLogService.log("STATUS_CHANGED", "User", user.getId());

        return mapper.toResponse(user);
    }

    @Transactional
    public void updatePassword(UUID id, String oldPassword, String newPassword) {
        User user = getUserByIdOrThrow(id);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Senha antiga incorreta");
        }

        Strength passwordStrength = passwordUtils.evaluate(newPassword);
        if (passwordStrength == Strength.VERY_WEAK || passwordStrength == Strength.WEAK) {
            throw new IllegalArgumentException("A nova senha é muito fraca");
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);
        // TODO: Invalidate all user sessions
        // TODO: Send password changed notification

        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o email: " + email));

        String token = passwordUtils.generateToken(user.getId());
        // TODO: Send password reset email
    }

    @Transactional
    public void changePasswordWithToken(String token, String newPassword) {
        String userIdStr = passwordUtils.validateToken(token);
        UUID userId = UUID.fromString(userIdStr);

        User user = getUserByIdOrThrow(userId);

        Strength passwordStrength = passwordUtils.evaluate(newPassword);
        if (passwordStrength == Strength.VERY_WEAK || passwordStrength == Strength.WEAK) {
            throw new IllegalArgumentException("A nova senha é muito fraca");
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);
    }

    @Transactional
    public UserResponse updateRole(UUID id, Integer roleId) {
        User user = getUserByIdOrThrow(id);

        validateUserCompany(user);
        validateMasterUser(user, id);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Função não encontrada com o id: " + roleId));

        if (role.getIsSystemRole() == false) {
            if (!role.getCompany().getId().equals(user.getCompany().getId())) {
                throw new IllegalStateException("A função não pertence à mesma empresa do usuário");
            }
        }

        // TODO: Check permissions for role assignment

        user.setRole(role);

        user = userRepository.save(user);

        activityLogService.log("PERMISSIONS_CHANGED", "User", user.getId());

        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateStatus(UUID id, UserStatus status) {
        User user = getUserByIdOrThrow(id);

        user.setStatus(status);

        if (status != UserStatus.OFFLINE) {
            user.setLastSeenAt(ZonedDateTime.now());
        }

        user = userRepository.save(user);

        activityLogService.log("STATUS_CHANGED", "User", user.getId());

        return mapper.toResponse(user);
    }

    @Transactional
    public void updateLastSeen(UUID id) {
        User user = getUserByIdOrThrow(id);

        user.setLastSeenAt(ZonedDateTime.now());
        userRepository.save(user);
    }

    public List<UserResponse> findByStatus(UserStatus status, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<User> users = userRepository.findByCompanyIdAndStatus(companyId, status, pageable);
        return users.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<UserResponse> search(String query) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<User> users;
        if (query == null || query.isBlank()) {
            users = userRepository.findByCompanyId(companyId);
        } else {
            users = userRepository.findAll(UserSpecification.searchByFields(query, companyId));
        }
        return users.stream()
                .map(mapper::toResponse)
                .toList();
    }

    private void validateUserCompany(User user) {
        UUID currentCompanyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (user.getCompany().getId() == null || !user.getCompany().getId().equals(currentCompanyId)) {
            throw new IllegalStateException("Usuário não pertence à empresa atual");
        }
    }

    private void validateMasterUser(User user, UUID currentUserId) {
        log.info(user.getId().toString());
        if (Boolean.TRUE.equals(user.getIsMaster()) && user.getId() != currentUserId) {
            throw new IllegalStateException("Não é possível editar um usuário master");
        }
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + id));
    }

    // TODO: Add method to get user permissions (receiveFromNumber role)
    // TODO: Add method to check if user has specific permission
    // TODO: Add method to get user statistics (tickets assigned, messages sent, etc.)
    // TODO: Add method to export user data (GDPR compliance)
}
