package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtParser {
    private static final Logger logger = LoggerFactory.getLogger(JwtParser.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
