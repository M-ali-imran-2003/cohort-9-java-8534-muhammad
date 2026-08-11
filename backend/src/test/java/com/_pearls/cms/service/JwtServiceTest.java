package com._pearls.cms.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", "dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3Jqd3R0ZXN0aW5ncHVycG9zZXM=");
        ReflectionTestUtils.setField(jwtService, "expiryMs", 3600000L);
    }

    @Test
    void testTokenGenerationAndExtraction(){

        // Arrange
        Long id = 1002L;

        // Act
        String token = jwtService.generateToken(id);
        Long fetchedId = Long.valueOf(jwtService.extractId(token));

        // Assert
        assertEquals(id, fetchedId);
    }

    @Test
    void testInvalidToken(){

        // Arrange
        String token = "abcd";

        // Assert
        assertThrows(JwtException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void testValidToken(){

        // Arrange
        String token = jwtService.generateToken(1005L);

        // Assert
        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void testTokenRemainingTime(){

        // Arrange
        String token = jwtService.generateToken(1005L);

        // Act
        long remainingTime = jwtService.getRemainingTimeInMilliseconds(token);

        // Assert
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime < 3600000L);
    }


}
