package com.vinisnzy.connectus_api.infra.utils;

import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.infra.security.dto.response.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Optional<AuthenticatedUser> getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public static UUID getCurrentCompanyIdOrThrow() {
        return getCurrentUser()
                .map(AuthenticatedUser::companyId)
                .orElseThrow(() -> new IllegalStateException("No authenticated company"));
    }

    public static UUID getCurrentUserIdOrThrow() {
        return getCurrentUser()
                .map(AuthenticatedUser::id)
                .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    }

    public static List<SimpleGrantedAuthority> getAuthoritiesByRole(Role role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        role.getPermissions().forEach((category, actions) -> actions.forEach((action, allowed) -> {
            if (Boolean.TRUE.equals(allowed)) {
                authorities.add(
                        new SimpleGrantedAuthority("PERM_" + category + "." + action)
                );
            }
        }));

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }
}
