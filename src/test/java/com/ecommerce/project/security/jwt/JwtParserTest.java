package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtParserTest {

    private static final String SECRET = "6d5f7d8e9a2b3c4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9";

    @InjectMocks
    private JwtParser jwtParser;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtParser, "jwtSecret", SECRET);
    }

    private String buildToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    @Test
    void getJwtFromHeader_validBearerToken_returnsToken() {
        String token = buildToken("alice");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        String result = jwtParser.getJwtFromHeader(request);

        assertEquals(token, result);
    }

    @Test
    void getJwtFromHeader_noAuthorizationHeader_returnsNull() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(jwtParser.getJwtFromHeader(request));
    }

    @Test
    void getJwtFromHeader_nonBearerPrefix_returnsNull() {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        assertNull(jwtParser.getJwtFromHeader(request));
    }

    @Test
    void getUserNameFromJwtToken_validToken_returnsUsername() {
        String token = buildToken("alice");

        String username = jwtParser.getUserNameFromJwtToken(token);

        assertEquals("alice", username);
    }
}
