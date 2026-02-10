package com.buyone.mediaservice.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CloudflareR2PropertiesTests {

    @Test
    void testNoArgsConstructor() {
        CloudflareR2Properties props = new CloudflareR2Properties();
        assertNotNull(props);
    }

    @Test
    void testSettersAndGetters() {
        CloudflareR2Properties props = new CloudflareR2Properties();
        
        props.setAccessKeyId("access-key");
        props.setSecretAccessKey("secret-key");
        props.setRegion("auto");
        props.setEndpoint("https://example.r2.cloudflarestorage.com");
        props.setBucket("my-bucket");
        
        assertEquals("access-key", props.getAccessKeyId());
        assertEquals("secret-key", props.getSecretAccessKey());
        assertEquals("auto", props.getRegion());
        assertEquals("https://example.r2.cloudflarestorage.com", props.getEndpoint());
        assertEquals("my-bucket", props.getBucket());
    }

    @Test
    void testEquality() {
        CloudflareR2Properties props1 = new CloudflareR2Properties();
        props1.setAccessKeyId("key");
        props1.setBucket("bucket");
        
        CloudflareR2Properties props2 = new CloudflareR2Properties();
        props2.setAccessKeyId("key");
        props2.setBucket("bucket");
        
        assertEquals(props1, props2);
        assertEquals(props1.hashCode(), props2.hashCode());
    }

    @Test
    void testToString() {
        CloudflareR2Properties props = new CloudflareR2Properties();
        props.setAccessKeyId("test-access");
        props.setBucket("test-bucket");
        
        String str = props.toString();
        assertNotNull(str);
        assertTrue(str.contains("test-access"));
        assertTrue(str.contains("test-bucket"));
    }

    @Test
    void testNullValues() {
        CloudflareR2Properties props = new CloudflareR2Properties();
        
        assertNull(props.getAccessKeyId());
        assertNull(props.getSecretAccessKey());
        assertNull(props.getRegion());
        assertNull(props.getEndpoint());
        assertNull(props.getBucket());
    }
}
