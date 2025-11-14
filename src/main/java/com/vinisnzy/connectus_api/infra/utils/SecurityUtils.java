package com.vinisnzy.connectus_api.infra.utils;

import com.vinisnzy.connectus_api.infra.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Optional<UserDetailsImpl> getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetailsImpl user) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public static UUID getCurrentCompanyIdOrThrow() {
        return getCurrentUser()
                .map(UserDetailsImpl::companyId)
                .orElseThrow(() -> new IllegalStateException("No authenticated company"));
    }

    public static UUID getCurrentUserIdOrThrow() {
        return getCurrentUser()
                .map(UserDetailsImpl::id)
                .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    }
}
