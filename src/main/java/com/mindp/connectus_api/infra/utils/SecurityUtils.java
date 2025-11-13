package com.mindp.connectus_api.infra.utils;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.core.entity.User;
import com.mindp.connectus_api.infra.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<User> getCurrentUser() {
        return getAuthentication()
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(UserDetailsImpl.class::isInstance)
                .map(principal -> ((UserDetailsImpl) principal).getUser());
    }

    public static Optional<Company> getCurrentCompany() {
        return getCurrentUser().map(User::getCompany);
    }

    public static boolean hasRole(String roleName) {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_" + roleName.toUpperCase()));
    }
}
