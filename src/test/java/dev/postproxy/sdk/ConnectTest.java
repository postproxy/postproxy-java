package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.ProfileGroupsResource;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConnectTest {

    @SuppressWarnings("unchecked")
    @Test
    void connectsBluesky() {
        var mock = new MockPostProxyClient(Map.of(
                "success", true,
                "profile", Map.of(
                        "id", "pf_bsky_1",
                        "network", "bluesky",
                        "name", "Jane",
                        "external_username", "jane.bsky.social"
                )
        ), 200, null);
        var groups = new ProfileGroupsResource(mock);

        var result = groups.connectBluesky("pg-1", "jane.bsky.social", "xxxx");

        assertTrue(result.success());
        assertEquals("pf_bsky_1", result.profile().id());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals(Map.of(
                "platform", "bluesky",
                "identifier", "jane.bsky.social",
                "app_password", "xxxx"
        ), body);
    }

    @SuppressWarnings("unchecked")
    @Test
    void connectsTelegram() {
        var mock = new MockPostProxyClient(Map.of(
                "success", true,
                "profile", Map.of(
                        "id", "pf_tg_1",
                        "network", "telegram",
                        "name", "My Bot",
                        "external_username", "my_bot"
                ),
                "next_step", "Add bot as admin"
        ), 200, null);
        var groups = new ProfileGroupsResource(mock);

        var result = groups.connectTelegram("pg-1", "123:ABC");

        assertTrue(result.nextStep().contains("admin"));
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals(Map.of(
                "platform", "telegram",
                "bot_token", "123:ABC"
        ), body);
    }
}
