package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.resource.ProfileGroupsResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGroupsResourceTest {

    private static final Map<String, Object> MOCK_GROUP = Map.of(
            "id", "pg-1",
            "name", "Test Group",
            "profiles_count", 3
    );

    @Test
    void listsProfileGroups() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(MOCK_GROUP)), 200, null);
        var groups = new ProfileGroupsResource(mock);
        var result = groups.list();
        assertEquals(1, result.data().size());
        assertEquals("Test Group", result.data().get(0).name());
    }

    @Test
    void getsProfileGroupById() {
        var mock = new MockPostProxyClient(MOCK_GROUP, 200, null);
        var groups = new ProfileGroupsResource(mock);
        var group = groups.get("pg-1");
        assertEquals(3, group.profilesCount());
        assertTrue(mock.getRequests().get(0).url().contains("/profile_groups/pg-1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsProfileGroup() {
        var mock = new MockPostProxyClient(MOCK_GROUP, 200, null);
        var groups = new ProfileGroupsResource(mock);
        var group = groups.create("Test Group");
        assertEquals("Test Group", group.name());
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        var pgBody = (Map<String, Object>) body.get("profile_group");
        assertEquals("Test Group", pgBody.get("name"));
    }

    @Test
    void deletesProfileGroup() {
        var mock = new MockPostProxyClient(Map.of("deleted", true), 200, null);
        var groups = new ProfileGroupsResource(mock);
        var result = groups.delete("pg-1");
        assertTrue(result.deleted());
        assertEquals("DELETE", mock.getRequests().get(0).method());
    }

    @SuppressWarnings("unchecked")
    @Test
    void initializesConnection() {
        var mock = new MockPostProxyClient(
                Map.of("url", "https://auth.example.com", "success", true), 200, null);
        var groups = new ProfileGroupsResource(mock);
        var conn = groups.initializeConnection("pg-1", Platform.INSTAGRAM, "https://callback.example.com");
        assertEquals("https://auth.example.com", conn.url());
        assertTrue(conn.success());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("/profile_groups/pg-1/initialize_connection"));
        var body = (Map<String, Object>) req.body();
        assertEquals("instagram", body.get("platform"));
        assertEquals("https://callback.example.com", body.get("redirect_url"));
    }
}
