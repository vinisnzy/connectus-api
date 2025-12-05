package com.vinisnzy.connectus_api.infra.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public record UserDetailsImpl(UUID id, UUID companyId, String roleName, String name, String email, String passwordHash,
                              Boolean isActive, Boolean isMaster,
                              Map<String, Map<String, Boolean>> permissions) implements UserDetails {

    public UserDetailsImpl(UUID id, UUID companyId, String roleName, String name, String email,
                           String passwordHash, Boolean isActive, Boolean isMaster,
                           Map<String, Map<String, Boolean>> permissions) {
        this.id = id;
        this.companyId = companyId;
        this.roleName = roleName;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
        this.isMaster = isMaster;
        this.permissions = permissions != null ? permissions : new HashMap<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Adicionar a role tradicional (ROLE_<nome>)
        String roleAuthority = roleName.toUpperCase();
        if (!roleAuthority.startsWith("ROLE_")) {
            roleAuthority = "ROLE_" + roleAuthority;
        }
        authorities.add(new SimpleGrantedAuthority(roleAuthority));

        // Adicionar as permissões baseadas em grupos e ações (PERM_<grupo>:<acao>)
        if (permissions != null && !permissions.isEmpty()) {
            permissions.forEach((group, actions) -> {
                if (actions != null) {
                    actions.forEach((action, hasPermission) -> {
                        if (Boolean.TRUE.equals(hasPermission)) {
                            String permissionAuthority = "PERM_" + group + "." + action;
                            authorities.add(new SimpleGrantedAuthority(permissionAuthority));
                        }
                    });
                }
            });
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
