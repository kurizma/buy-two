package com.buyone.mediaservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTests {

    @Test
    void testJwtDecoderCreation() {
        JwtConfig config = new JwtConfig();
        String testSecret = "testsecretkey1234567890123456789012345678901234567890";
        ReflectionTestUtils.setField(config, "jwtSecret", testSecret);
        
        JwtDecoder decoder = config.jwtDecoder();
        
        assertNotNull(decoder);
    }

    @Test
    void testJwtDecoderWithDifferentSecrets() {
        JwtConfig config1 = new JwtConfig();
        JwtConfig config2 = new JwtConfig();
        
        String secret1 = "secret1234567890123456789012345678901234567890";
        String secret2 = "different1234567890123456789012345678901234567";
        
        ReflectionTestUtils.setField(config1, "jwtSecret", secret1);
        ReflectionTestUtils.setField(config2, "jwtSecret", secret2);
        
        JwtDecoder decoder1 = config1.jwtDecoder();
        JwtDecoder decoder2 = config2.jwtDecoder();
        
        assertNotNull(decoder1);
        assertNotNull(decoder2);
        assertNotSame(decoder1, decoder2);
    }
}
