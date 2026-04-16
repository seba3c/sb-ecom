package com.ecommerce.project.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private JwtParser jwtParser;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtUtils jwtUtils;

    @Test
    void getJwtFromHeader_delegatesToJwtParser() {
        when(jwtParser.getJwtFromHeader(request)).thenReturn("sometoken");

        String result = jwtUtils.getJwtFromHeader(request);

        assertEquals("sometoken", result);
        verify(jwtParser).getJwtFromHeader(request);
    }

    @Test
    void getUserNameFromJwtToken_delegatesToJwtParser() {
        when(jwtParser.getUserNameFromJwtToken("token")).thenReturn("alice");

        String result = jwtUtils.getUserNameFromJwtToken("token");

        assertEquals("alice", result);
        verify(jwtParser).getUserNameFromJwtToken("token");
    }

    @Test
    void validateJwtToken_delegatesToJwtValidator() {
        when(jwtValidator.validateJwtToken("token")).thenReturn(true);

        boolean result = jwtUtils.validateJwtToken("token");

        assertTrue(result);
        verify(jwtValidator).validateJwtToken("token");
    }

    @Test
    void generateTokenFromUsername_delegatesToJwtGenerator() {
        UserDetails userDetails = new User("bob", "pass", Collections.emptyList());
        when(jwtGenerator.generateTokenFromUsername(userDetails)).thenReturn("generated-token");

        String result = jwtUtils.generateTokenFromUsername(userDetails);

        assertEquals("generated-token", result);
        verify(jwtGenerator).generateTokenFromUsername(userDetails);
    }
}
