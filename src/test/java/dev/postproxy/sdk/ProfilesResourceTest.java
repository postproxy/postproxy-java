package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.ProfilesResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProfilesResourceTest {

    private static final Map<String, Object> MOCK_PROFILE = Map.ofEntries(
            Map.entry("id", "prof-1"),
            Map.entry("name", "Test Profile"),
            Map.entry("status", "active"),
            Map.entry("platform", "instagram"),
            Map.entry("profile_group_id", "pg-1"),
            Map.entry("post_count", 5)
    );

    @Test
    void listsProfiles() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(MOCK_PROFILE)), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.list();
        assertEquals(1, result.data().size());
        assertEquals("prof-1", result.data().get(0).id());
        assertEquals("instagram", result.data().get(0).platform().getValue());
    }

    @Test
    void getsProfileById() {
        var mock = new MockPostProxyClient(MOCK_PROFILE, 200, null);
        var profiles = new ProfilesResource(mock);
        var profile = profiles.get("prof-1");
        assertEquals("Test Profile", profile.name());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/prof-1"));
    }

    @Test
    void getsPlacementsForProfile() {
        var mock = new MockPostProxyClient(
                Map.of("data", List.of(Map.of("id", "pl-1", "name", "Feed"))), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.placements("prof-1");
        assertEquals(1, result.data().size());
        assertEquals("Feed", result.data().get(0).name());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/prof-1/placements"));
    }

    @Test
    void deletesProfile() {
        var mock = new MockPostProxyClient(Map.of("success", true), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.delete("prof-1");
        assertTrue(result.success());
        assertEquals("DELETE", mock.getRequests().get(0).method());
    }
}
