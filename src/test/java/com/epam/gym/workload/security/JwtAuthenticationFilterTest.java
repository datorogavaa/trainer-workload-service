package com.epam.gym.workload.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

    @AfterEach
    void clearSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenCreatesServiceAuthentication() throws Exception {
        when(jwtService.validateAndGetSubject("signed-token")).thenReturn(java.util.Optional.of("main-service"));
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer signed-token");
        var response = new MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);

        filter.doFilter(request, response, chain);

        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("main-service", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SERVICE")));
        verify(chain).doFilter(request, response);
    }

    @Test
    void missingOrInvalidBearerTokenLeavesContextUnauthenticated() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic credentials");
        var response = new MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication());
        verifyNoInteractions(jwtService);
        verify(chain).doFilter(request, response);
    }
}
