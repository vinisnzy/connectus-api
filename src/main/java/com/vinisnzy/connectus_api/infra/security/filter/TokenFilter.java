package com.vinisnzy.connectus_api.infra.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.infra.security.service.TokenService;
import com.vinisnzy.connectus_api.infra.security.dto.response.AuthenticatedUser;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoveryToken(request);

        if (token != null) {
            try {
                DecodedJWT decodedJWT = tokenService.validateToken(token);

                UUID userId = UUID.fromString(decodedJWT.getSubject());
                Integer roleId = decodedJWT.getClaim("roleId").asInt();

                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Função não encontrada com o id: " + roleId));

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + userId));

                List<SimpleGrantedAuthority> authorities = SecurityUtils.getAuthoritiesByRole(role);

                Authentication authentication = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(userId, user.getCompany().getId(), roleId), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception _) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoveryToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
