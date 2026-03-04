package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Webhook;
import dev.postproxy.sdk.model.WebhookDelivery;
import dev.postproxy.sdk.param.CreateWebhookParams;
import dev.postproxy.sdk.param.UpdateWebhookParams;
import dev.postproxy.sdk.resource.WebhooksResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebhooksResourceTest {

    private static final Map<String, Object> MOCK_WEBHOOK = Map.of(
            "id", "wh-1",
            "url", "https://example.com/webhook",
            "events", List.of("post.published", "post.failed"),
            "enabled", true,
            "description", "Test webhook",
            "secret", "whsec_test123",
            "created_at", "2025-01-01T00:00:00Z",
            "updated_at", "2025-01-01T00:00:00Z"
    );

    private static final Map<String, Object> MOCK_DELIVERY = Map.of(
            "id", "del-1",
            "event_id", "evt-1",
            "event_type", "post.published",
            "response_status", 200,
            "attempt_number", 1,
            "success", true,
            "attempted_at", "2025-01-01T00:00:00Z",
            "created_at", "2025-01-01T00:00:00Z"
    );

    @Test
    void listsWebhooks() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(MOCK_WEBHOOK)), 200, null);
        var webhooks = new WebhooksResource(mock);

        var result = webhooks.list();
        assertEquals(1, result.data().size());
        assertEquals("wh-1", result.data().get(0).id());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/webhooks"));
    }

    @Test
    void getsWebhookById() {
        var mock = new MockPostProxyClient(MOCK_WEBHOOK, 200, null);
        var webhooks = new WebhooksResource(mock);

        var webhook = webhooks.get("wh-1");
        assertEquals("wh-1", webhook.id());
        assertEquals("https://example.com/webhook", webhook.url());
        assertEquals(List.of("post.published", "post.failed"), webhook.events());
        assertTrue(webhook.enabled());
        assertTrue(mock.getRequests().get(0).url().contains("/webhooks/wh-1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsWebhook() {
        var mock = new MockPostProxyClient(MOCK_WEBHOOK, 200, null);
        var webhooks = new WebhooksResource(mock);

        var webhook = webhooks.create(CreateWebhookParams.builder()
                .url("https://example.com/webhook")
                .events(List.of("post.published", "post.failed"))
                .description("Test webhook")
                .build());

        assertEquals("wh-1", webhook.id());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("https://example.com/webhook", body.get("url"));
        assertEquals(List.of("post.published", "post.failed"), body.get("events"));
        assertEquals("Test webhook", body.get("description"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void updatesWebhook() {
        var mock = new MockPostProxyClient(
                Map.of("id", "wh-1", "enabled", false, "events", List.of(), "url", "", "created_at", "", "updated_at", ""),
                200, null);
        var webhooks = new WebhooksResource(mock);

        var webhook = webhooks.update("wh-1", UpdateWebhookParams.builder()
                .enabled(false)
                .build());

        assertFalse(webhook.enabled());

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        var body = (Map<String, Object>) req.body();
        assertEquals(false, body.get("enabled"));
    }

    @Test
    void deletesWebhook() {
        var mock = new MockPostProxyClient(Map.of("deleted", true), 200, null);
        var webhooks = new WebhooksResource(mock);

        var result = webhooks.delete("wh-1");
        assertTrue(result.deleted());
        assertEquals("DELETE", mock.getRequests().get(0).method());
    }

    @Test
    void listsDeliveries() {
        var mock = new MockPostProxyClient(
                Map.of("data", List.of(MOCK_DELIVERY), "total", 1, "page", 1, "per_page", 10),
                200, null);
        var webhooks = new WebhooksResource(mock);

        var result = webhooks.deliveries("wh-1", 1, 10);
        assertEquals(1, result.data().size());
        assertEquals(1, result.data().get(0).id());
        assertEquals("post.published", result.data().get(0).eventType());
        assertTrue(result.data().get(0).success());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/webhooks/wh-1/deliveries"));
        assertTrue(url.contains("page=1"));
        assertTrue(url.contains("per_page=10"));
    }

    @Test
    void verifiesValidSignature() {
        String payload = "{\"event\":\"post.published\",\"data\":{\"id\":\"post-1\"}}";
        String secret = "whsec_test123";
        String signature = "t=1234567890,v1=c8e99efbb07ac8e3152c02dd8d83e8ddb803ae8fb001d9e1ab42fb0b1f405ef2";
        assertTrue(WebhookSignature.verify(payload, signature, secret));
    }

    @Test
    void rejectsInvalidSignature() {
        String payload = "{\"event\":\"post.published\",\"data\":{\"id\":\"post-1\"}}";
        String secret = "whsec_test123";
        String signature = "t=1234567890,v1=invalidsignature";
        assertFalse(WebhookSignature.verify(payload, signature, secret));
    }

    @Test
    void rejectsWrongSecret() {
        String payload = "{\"event\":\"post.published\",\"data\":{\"id\":\"post-1\"}}";
        String secret = "wrong_secret";
        String signature = "t=1234567890,v1=c8e99efbb07ac8e3152c02dd8d83e8ddb803ae8fb001d9e1ab42fb0b1f405ef2";
        assertFalse(WebhookSignature.verify(payload, signature, secret));
    }
}
