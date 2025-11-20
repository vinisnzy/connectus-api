package com.vinisnzy.connectus_api.infra.websocket;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.infra.security.dto.response.AuthenticatedUser;
import com.vinisnzy.connectus_api.infra.security.service.TokenService;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token JWT ausente ou inválido");
            }

            token = token.substring(7);

            DecodedJWT decodedJWT = tokenService.validateToken(token);

            UUID userId = UUID.fromString(decodedJWT.getSubject());
            Integer roleId = decodedJWT.getClaim("roleId").asInt();

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Função não encontrada com o id: " + roleId));

            List<SimpleGrantedAuthority> authorities = SecurityUtils.getAuthoritiesByRole(role);

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(userId, roleId), null, authorities);

            accessor.setUser(authentication);
        }

        return message;
    }
}
