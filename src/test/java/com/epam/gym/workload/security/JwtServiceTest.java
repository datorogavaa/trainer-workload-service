package com.epam.gym.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "a-secret-key-that-is-at-least-256-bits-long-for-tests".getBytes());
    private final JwtService service = new JwtService(SECRET);

    @Test
    void validatesSignedTokenAndReturnsSubject() {
        var key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        var token = Jwts.builder().subject("main-service").signWith(key).compact();

        assertEquals("main-service", service.validateAndGetSubject(token).orElseThrow());
    }

    @Test
    void rejectsMalformedToken() {
        assertTrue(service.validateAndGetSubject("not-a-jwt").isEmpty());
    }

    @Test
    void rejectsTokenWithoutSubject() {
        var key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        var token = Jwts.builder().signWith(key).compact();

        assertTrue(service.validateAndGetSubject(token).isEmpty());
    }
}
