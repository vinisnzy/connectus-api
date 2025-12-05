package com.vinisnzy.connectus_api.infra.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vinisnzy.connectus_api.shared.enums.Strength;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PasswordUtils {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UUID userId) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("type", "password_reset")
                .withExpiresAt(Instant.now().plusSeconds(900))
                .sign(algorithm);
    }

    public String validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        var jwt = JWT.require(algorithm)
                .withClaim("type", "password_reset")
                .build()
                .verify(token);
        return jwt.getSubject();
    }

    public Strength evaluate(String password) {

        int score = 0;

        if (password == null) return Strength.VERY_WEAK;

        // Critério 1: tamanho
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Critério 2: variedade de caracteres
        if (password.matches(".*[a-z].*")) score++;        // minúsculas
        if (password.matches(".*[A-Z].*")) score++;        // maiúsculas
        if (password.matches(".*\\d.*")) score++;          // dígitos
        if (password.matches(".*[^a-zA-Z0-9].*")) score++; // símbolos

        // Resultado baseado no score
        if (score <= 2) return Strength.VERY_WEAK;
        if (score == 3) return Strength.WEAK;
        if (score == 4) return Strength.MEDIUM;
        if (score == 5) return Strength.STRONG;
        return Strength.VERY_STRONG; // 6 ou 7
    }
}
