package dev.postproxy.sdk;

import dev.postproxy.sdk.exception.ConflictException;
import dev.postproxy.sdk.model.PostSyncStatus;
import dev.postproxy.sdk.model.PostSyncTrigger;
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

    private static final Map<String, Object> MOCK_POST_SYNC = Map.ofEntries(
            Map.entry("id", "sync456def"),
            Map.entry("profile_id", "prof-1"),
            Map.entry("kind", "posts"),
            Map.entry("trigger", "backfill"),
            Map.entry("status", "running"),
            Map.entry("started_at", "2026-08-06T09:15:02.000Z"),
            Map.entry("posts_seen", 150),
            Map.entry("posts_imported", 143),
            Map.entry("backfill_from", "2025-01-01T00:00:00.000Z"),
            Map.entry("oldest_posted_at", "2025-11-04T18:22:00.000Z"),
            Map.entry("created_at", "2026-08-06T09:15:00.000Z")
    );

    @Test
    void startsAPostsBackfill() {
        var response = new java.util.LinkedHashMap<String, Object>(MOCK_POST_SYNC);
        response.put("status", "pending");
        var mock = new MockPostProxyClient(response, 202, null);
        var profiles = new ProfilesResource(mock);

        var sync = profiles.backfillPosts("prof-1", "2025-01-01");

        assertEquals("sync456def", sync.id());
        assertEquals(PostSyncTrigger.BACKFILL, sync.trigger());
        assertEquals(PostSyncStatus.PENDING, sync.status());

        var request = mock.getRequests().get(0);
        assertEquals("POST", request.method());
        assertTrue(request.url().contains("/profiles/prof-1/backfill_posts"));
        assertEquals(Map.of("from", "2025-01-01"), request.body());
    }

    @Test
    void sendsIdempotencyKeyWithBackfill() {
        var mock = new MockPostProxyClient(MOCK_POST_SYNC, 202, null);
        var profiles = new ProfilesResource(mock);

        profiles.backfillPosts("prof-1", "2025-01-01", null, "key-1");

        assertEquals("key-1", mock.getRequests().get(0).idempotencyKey());
    }

    @Test
    void throwsConflictWhenBackfillAlreadyRunning() {
        var mock = new MockPostProxyClient(
                Map.of("error", "A posts backfill is already running for this profile",
                        "profile_sync_id", "sync456def"),
                409, null);
        var profiles = new ProfilesResource(mock);

        var e = assertThrows(ConflictException.class, () -> profiles.backfillPosts("prof-1", "2025-01-01"));
        assertEquals(409, e.getStatusCode());
        assertEquals("sync456def", e.getResponse().get("profile_sync_id"));
    }

    @Test
    void listsPostSyncsWithFilters() {
        var mock = new MockPostProxyClient(
                Map.of("total", 1, "page", 0, "per_page", 25, "data", List.of(MOCK_POST_SYNC)), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.postSyncs("prof-1", PostSyncTrigger.BACKFILL, PostSyncStatus.RUNNING, null, 25, null);

        assertEquals(1, result.total());
        assertEquals(143, result.data().get(0).postsImported());
        assertEquals("2025-11-04T18:22:00.000Z", result.data().get(0).oldestPostedAt());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/profiles/prof-1/post_syncs"));
        assertTrue(url.contains("trigger=backfill"));
        assertTrue(url.contains("status=running"));
        assertTrue(url.contains("per_page=25"));
    }

    @Test
    void getsASinglePostSync() {
        var response = new java.util.LinkedHashMap<String, Object>(MOCK_POST_SYNC);
        response.put("status", "completed");
        var mock = new MockPostProxyClient(response, 200, null);
        var profiles = new ProfilesResource(mock);

        var sync = profiles.postSync("prof-1", "sync456def");

        assertEquals(PostSyncStatus.COMPLETED, sync.status());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/prof-1/post_syncs/sync456def"));
    }
}
