package com.epam.gym.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private final javax.crypto.SecretKey signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final JwtService service = new JwtService(Encoders.BASE64.encode(signingKey.getEncoded()));

    @Test
    void returnsSubjectForValidToken() {
        String token = Jwts.builder()
                .subject("service-user")
                .signWith(signingKey)
                .compact();

        assertEquals("service-user", service.validateAndGetSubject(token).orElseThrow());
    }

    @Test
    void rejectsMalformedToken() {
        assertTrue(service.validateAndGetSubject("not-a-jwt").isEmpty());
    }

    @Test
    void rejectsExpiredToken() {
        String token = Jwts.builder()
                .subject("service-user")
                .expiration(Date.from(Instant.now().minusSeconds(1)))
                .signWith(signingKey)
                .compact();

        assertTrue(service.validateAndGetSubject(token).isEmpty());
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String token = Jwts.builder()
                .subject("service-user")
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .compact();

        assertTrue(service.validateAndGetSubject(token).isEmpty());
    }
}
