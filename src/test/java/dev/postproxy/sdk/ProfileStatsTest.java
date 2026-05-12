package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.ProfilesResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProfileStatsTest {

    @Test
    void getsProfileStatsWithPlacement() {
        var mock = new MockPostProxyClient(Map.of(
                "data", Map.of(
                        "profile_id", "pf1",
                        "platform", "linkedin",
                        "placement_id", "org_1",
                        "records", List.of(Map.of(
                                "stats", Map.of("followerCount", 100),
                                "recorded_at", "2026-05-12T00:00:00Z"
                        ))
                )
        ), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.getProfileStats("pf1", "org_1", "2026-04-01T00:00:00Z", null);

        assertEquals("pf1", result.data().profileId());
        assertEquals(100, ((Number) result.data().records().get(0).stats().get("followerCount")).intValue());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("/profiles/pf1/stats"));
        assertTrue(req.url().contains("placement_id=org_1"));
        assertTrue(req.url().contains("from=2026-04-01"));
    }

    @Test
    void getsProfileStatsWithoutPlacement() {
        var mock = new MockPostProxyClient(Map.of(
                "data", Map.of(
                        "profile_id", "bsky1",
                        "platform", "bluesky",
                        "records", List.of()
                )
        ), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.getProfileStats("bsky1", null);
        assertNull(result.data().placementId());

        assertFalse(mock.getRequests().get(0).url().contains("placement_id"));
    }
}
