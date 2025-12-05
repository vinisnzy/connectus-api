package com.vinisnzy.connectus_api.infra.security.annotations;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("permissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    /**
     * Verifica se o usuário autenticado possui a permissão especificada.
     *
     * @param authentication o objeto de autenticação do Spring Security
     * @param group          o grupo de permissão (ex: "tickets", "reports")
     * @param action         a ação (ex: "view", "edit", "create")
     * @return true se o usuário possui a permissão, false caso contrário
     */
    public boolean hasPermission(Authentication authentication, String group, String action) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String requiredAuthority = "PERM_" + group + "." + action;

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(requiredAuthority));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Este método pode ser usado para verificações mais complexas baseadas em objetos
        // Por enquanto, retornamos false, pois não estamos usando este padrão
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        // Este método pode ser usado para verificações baseadas em IDs de objetos
        // Por enquanto, retornamos false, pois não estamos usando este padrão
        return false;
    }
}
