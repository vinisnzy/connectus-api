package com.vinisnzy.connectus_api.domain.core.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.dto.request.CreateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateRolePermissionsRequest;
import com.vinisnzy.connectus_api.domain.core.dto.request.UpdateRoleRequest;
import com.vinisnzy.connectus_api.domain.core.dto.response.RoleResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.mapper.RoleMapper;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.infra.utils.JsonUtils;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final RoleMapper mapper;

    public List<RoleResponse> findAll() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return roleRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public RoleResponse findById(Integer id) {
        Role role = getRoleByIdOrThrow(id);
        return mapper.toResponse(role);
    }

    public List<RoleResponse> findSystemRoles() {
        return roleRepository.findByIsSystemRole()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        if (roleRepository.existsByNameAndCompanyId(request.name(), companyId)) {
            throw new IllegalArgumentException("O nome do cargo já existe na empresa.");
        }

        JsonUtils.validateRolePermissionsJson(request.permissions());

        Role role = mapper.toEntity(request);
        role = roleRepository.save(role);
        return mapper.toResponse(role);
    }

    @Transactional
    public RoleResponse update(Integer id, UpdateRoleRequest updatedRole) {
        Role role = getRoleByIdOrThrow(id);

        validateRoleByCurrentCompany(role);

        if (roleRepository.existsByNameAndCompanyId(updatedRole.name(), role.getCompany().getId())
                && !role.getName().equals(updatedRole.name())) {
            throw new IllegalArgumentException("O nome do cargo já existe na empresa.");
        }

        // TODO: Validate permissions structure
        JsonUtils.validateRolePermissionsJson(updatedRole.permissions());

        mapper.updateEntity(updatedRole, role);

        role = roleRepository.save(role);
        return mapper.toResponse(role);
    }

    @Transactional
    public void delete(Integer id) {
        Role role = getRoleByIdOrThrow(id);

        validateRoleByCurrentCompany(role);

        if (!userRepository.findByRoleId(role.getId(), PageRequest.of(0, 10)).getContent().isEmpty()) {
            throw new IllegalArgumentException("Não é possível deletar um cargo atribuído a usuários.");
        }

        roleRepository.deleteById(id);
    }

    @Transactional
    public RoleResponse updatePermissions(Integer id, UpdateRolePermissionsRequest permissionsData) {
        Role role = getRoleByIdOrThrow(id);

        JsonUtils.validateRolePermissionsJson(permissionsData.permissions());

        if (Boolean.TRUE.equals(role.getIsSystemRole())) {
            throw new IllegalArgumentException("Não é possível alterar as permissões de um cargo do sistema.");
        }

        role.setPermissions(permissionsData.permissions());

        role = roleRepository.save(role);
        return mapper.toResponse(role);
    }

    public boolean hasPermission(Integer roleId, String resource, String action) {
        Role role = getRoleByIdOrThrow(roleId);

        return role.getPermissions().getOrDefault(resource, Map.of())
                .getOrDefault(action, false);
    }

    @Transactional
    public RoleResponse clone(Integer id, String newName) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();

        Role originalRole = getRoleByIdOrThrow(id);

        if (roleRepository.existsByNameAndCompanyId(newName, companyId)) {
            throw new IllegalArgumentException("O nome do cargo já existe na empresa.");
        }

        Role newRole = new Role();
        newRole.setCompany(companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId)));
        newRole.setName(newName);
        newRole.setPermissions(originalRole.getPermissions());
        newRole.setIsSystemRole(false);

        newRole = roleRepository.save(newRole);
        return mapper.toResponse(newRole);
    }

    private Role getRoleByIdOrThrow(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com o id: " + id));
    }

    private void validateRoleByCurrentCompany(Role role) {
        if (role.getCompany().getId() != SecurityUtils.getCurrentCompanyIdOrThrow()) {
            throw new EntityNotFoundException("Cargo não pertence à empresa do usuário autenticado.");
        }
    }

    // TODO: Add method to create default roles for new companies
    // TODO: Add method to sync system roles across all companies
    // TODO: Add method to get all permissions available in the system
}
