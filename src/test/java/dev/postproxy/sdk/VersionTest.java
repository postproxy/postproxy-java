package dev.postproxy.sdk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    void versionIsBumped() {
        assertEquals("1.7.0", Version.VERSION);
    }

    @Test
    void clientExposesUserAgent() {
        var client = new PostProxyClient("test", "https://api.test", null);
        String ua = client.getUserAgent();
        assertTrue(ua.startsWith("postproxy-java/" + Version.VERSION));
        assertTrue(ua.contains("jvm/"));
    }
}
