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
    void assignsPlacementToGroup() {
        var mock = new MockPostProxyClient(Map.of(
                "id", "pl-1",
                "name", "Feed",
                "metadata", Map.of(),
                "profile_group_id", "pg-2"
        ), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.assignPlacementToGroup("prof-1", "pl-1", "pg-2");
        assertEquals("pg-2", result.profileGroupId());
        var request = mock.getRequests().get(0);
        assertEquals("PATCH", request.method());
        assertTrue(request.url().contains("/profiles/prof-1/assign_placement_to_group"));
        assertEquals(Map.of("placement_id", "pl-1", "target_profile_group_id", "pg-2"), request.body());
    }

    @Test
    void listsIceBreakers() {
        var mock = new MockPostProxyClient(Map.of(
                "ice_breakers", List.of(Map.of("question", "What do you do?", "payload", "services"))
        ), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.iceBreakers("prof-1");
        assertEquals(1, result.iceBreakers().size());
        assertEquals("What do you do?", result.iceBreakers().get(0).question());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/prof-1/ice_breakers"));
    }

    @Test
    void setsIceBreakers() {
        var mock = new MockPostProxyClient(Map.of("success", true), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.setIceBreakers("prof-1",
                List.of(new dev.postproxy.sdk.model.IceBreaker("What do you do?", "services")));
        assertTrue(result.success());
        var request = mock.getRequests().get(0);
        assertEquals("POST", request.method());
        assertTrue(request.url().contains("/profiles/prof-1/ice_breakers"));
    }

    @Test
    void deletesIceBreakers() {
        var mock = new MockPostProxyClient(Map.of("success", true), 200, null);
        var profiles = new ProfilesResource(mock);
        var result = profiles.deleteIceBreakers("prof-1");
        assertTrue(result.success());
        assertEquals("DELETE", mock.getRequests().get(0).method());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/prof-1/ice_breakers"));
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
