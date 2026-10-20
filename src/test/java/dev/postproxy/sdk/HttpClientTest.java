package dev.postproxy.sdk;

import com.sun.net.httpserver.HttpServer;
import dev.postproxy.sdk.exception.ForbiddenException;
import dev.postproxy.sdk.exception.RateLimitException;
import dev.postproxy.sdk.param.CreatePostParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Exercises the real HTTP path against a local server. */
class HttpClientTest {

    private HttpServer server;
    private final List<Map<String, List<String>>> receivedHeaders = new ArrayList<>();

    private PostProxy start(int status, String contentType, byte[] body, Map<String, String> headers) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            receivedHeaders.add(Map.copyOf(exchange.getRequestHeaders()));
            exchange.getRequestBody().readAllBytes();
            exchange.getResponseHeaders().add("Content-Type", contentType);
            headers.forEach((k, v) -> exchange.getResponseHeaders().add(k, v));
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        return PostProxy.builder("k").baseUrl("http://127.0.0.1:" + server.getAddress().getPort()).build();
    }

    private static byte[] json(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @AfterEach
    void stop() {
        if (server != null) server.stop(0);
    }

    @Test
    void throwsForbiddenExceptionOn403() throws IOException {
        var client = start(403, "application/json", json("{\"error\":\"Account is past due\"}"), Map.of());
        var e = assertThrows(ForbiddenException.class, () -> client.profiles().list());
        assertEquals(403, e.getStatusCode());
        assertEquals("Account is past due", e.getMessage());
    }

    @Test
    void throwsRateLimitExceptionWithRetryAfter() throws IOException {
        var client = start(429, "application/json", json("{\"error\":\"rate limited\"}"), Map.of("Retry-After", "30"));
        var e = assertThrows(RateLimitException.class, () -> client.profiles().list());
        assertEquals(429, e.getStatusCode());
        assertEquals(30, e.getRetryAfter());
    }

    @Test
    void retryAfterIsNullWhenMissing() throws IOException {
        var client = start(429, "application/json", json("{\"error\":\"rate limited\"}"), Map.of());
        var e = assertThrows(RateLimitException.class, () -> client.profiles().list());
        assertNull(e.getRetryAfter());
    }

    @Test
    void sendsSkipDuplicateCheckHeader() throws IOException {
        var client = start(200, "application/json", json("{\"id\":\"p1\"}"), Map.of());

        client.posts().create(CreatePostParams.builder().body("hello").profiles(List.of("profile-1"))
                .skipDuplicateCheck(true).build());
        client.posts().create(CreatePostParams.builder().body("hello").profiles(List.of("profile-1")).build());

        assertEquals(List.of("true"), receivedHeaders.get(0).get("X-skip-duplicate-check"));
        assertNull(receivedHeaders.get(1).get("X-skip-duplicate-check"));
    }

    @Test
    void returnsRawBytesForWhatsAppMedia() throws IOException {
        var client = start(200, "image/jpeg", new byte[]{1, 2, 3}, Map.of());

        var media = client.whatsapp().media("prof-1", "pn-1", "media-1");
        assertEquals("image/jpeg", media.contentType());
        assertArrayEquals(new byte[]{1, 2, 3}, media.data());
    }

    @Test
    void rawDownloadMapsErrors() throws IOException {
        var client = start(404, "application/json", json("{\"error\":\"Media not found\"}"), Map.of());
        var e = assertThrows(dev.postproxy.sdk.exception.NotFoundException.class,
                () -> client.whatsapp().media("prof-1", "pn-1", "media-1"));
        assertEquals("Media not found", e.getMessage());
    }
}
