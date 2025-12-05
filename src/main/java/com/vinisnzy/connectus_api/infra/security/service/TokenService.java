package com.vinisnzy.connectus_api.infra.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final String issuer;
    private final Algorithm algorithm;

    public TokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public String generateToken(User user) {
        boolean subscriptionActive = false;
        if (user.getCompany().getSubscriptions() != null && !user.getCompany().getSubscriptions().isEmpty()) {
            subscriptionActive = user.getCompany().getSubscriptions().stream()
                    .anyMatch(subscription ->
                            subscription.getStatus() == SubscriptionStatus.ACTIVE ||
                            subscription.getStatus() == SubscriptionStatus.TRIAL
                    );
        }
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getId().toString())
                .withClaim("roleId", user.getRole().getId())
                .withClaim("subscriptionActive", subscriptionActive)
                .withExpiresAt(expiresAt())
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException _) {
            throw new JWTVerificationException("Erro ao validar token JWT");
        }
    }

    private Instant expiresAt() {
        return Instant.now().plus(30, ChronoUnit.MINUTES);
    }
}
