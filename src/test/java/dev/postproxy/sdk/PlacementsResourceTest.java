package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.ApiKeysResource;
import dev.postproxy.sdk.resource.PlacementsResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlacementsResourceTest {

    private static final Map<String, Object> MOCK_PLACEMENT = Map.of(
            "id", "pl_1",
            "platform", "facebook",
            "external_id", "1234567890",
            "name", "Acme Page",
            "status", "active",
            "active_profile", Map.of("id", "prof_1", "name", "Jane", "connected", true),
            "linked_profiles", List.of(Map.of("id", "prof_2", "name", "John", "connected", true))
    );

    @Test
    void listsPlacements() {
        var mock = new MockPostProxyClient(
                Map.of("total", 1, "page", 1, "per_page", 25, "data", List.of(MOCK_PLACEMENT)), 200, "pg-default");
        var placements = new PlacementsResource(mock);

        var result = placements.list(2, 10);
        assertEquals("prof_1", result.data().get(0).activeProfile().id());
        assertEquals("John", result.data().get(0).linkedProfiles().get(0).name());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/api/placements?"));
        assertTrue(req.url().contains("page=2"));
        assertTrue(req.url().contains("per_page=10"));
        assertFalse(req.url().contains("profile_group_id"));
    }

    @Test
    void setsActiveProfile() {
        var mock = new MockPostProxyClient(MOCK_PLACEMENT, 200, null);
        var placements = new PlacementsResource(mock);

        placements.setActiveProfile("pl_1", "prof_2");

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        assertTrue(req.url().contains("/placements/pl_1/set_active_profile"));
        assertEquals(Map.of("profile_id", "prof_2"), req.body());
    }

    @Test
    void listsApiKeys() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(Map.of("id", "k1", "name", "CI",
                "profile_group_id", "pg_1", "sandbox", false, "created_at", "2026-09-09T00:00:00Z"))), 200, null);
        var apiKeys = new ApiKeysResource(mock);

        var result = apiKeys.list();
        assertEquals("CI", result.data().get(0).name());
        assertNull(result.data().get(0).token());
        assertTrue(mock.getRequests().get(0).url().contains("/api/api_keys"));
    }

    @Test
    void createsApiKey() {
        var mock = new MockPostProxyClient(Map.of("id", "k1", "name", "CI", "profile_group_id", "pg_1",
                "sandbox", false, "token", "secret", "created_at", "2026-09-09T00:00:00Z"), 200, null);
        var apiKeys = new ApiKeysResource(mock);

        var key = apiKeys.create("CI", "pg_1", "idem");
        assertEquals("secret", key.token());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertEquals(Map.of("name", "CI", "profile_group_id", "pg_1"), req.body());
        assertEquals("idem", req.idempotencyKey());
    }

    @Test
    void deletesApiKey() {
        var mock = new MockPostProxyClient(Map.of("deleted", true), 200, null);
        var apiKeys = new ApiKeysResource(mock);

        assertTrue(apiKeys.delete("k1").deleted());
        var req = mock.getRequests().get(0);
        assertEquals("DELETE", req.method());
        assertTrue(req.url().contains("/api_keys/k1"));
    }
}
