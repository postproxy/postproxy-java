package dev.postproxy.sdk;

import dev.postproxy.sdk.exception.ConflictException;
import dev.postproxy.sdk.model.PostSyncStatus;
import dev.postproxy.sdk.model.PostSyncTrigger;
import dev.postproxy.sdk.resource.ProfilesResource;
import dev.postproxy.sdk.model.DailyStatsAttribution;
import dev.postproxy.sdk.model.InstagramAudioType;
import dev.postproxy.sdk.model.ProfileInactiveReason;
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

    @Test
    void fetchesDailyStatsWithAttribution() {
        var mock = new MockPostProxyClient(Map.of("data", Map.of(
                "profile_id", "p1", "attribution", "publish",
                "records", List.of(Map.of("date", "2026-09-01", "impressions", 10, "platform_stats", Map.of())),
                "metrics", Map.of())), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.dailyStats("p1", "pl1", "2026-09-01", "2026-09-30", DailyStatsAttribution.PUBLISH, null);
        var record = result.data().records().get(0);
        assertEquals("2026-09-01", record.date());
        assertEquals(10, ((Number) record.metric("impressions")).intValue());
        assertFalse(record.metrics().containsKey("date"));
        assertEquals(DailyStatsAttribution.PUBLISH, result.data().attribution());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/api/profiles/p1/daily_stats?"));
        assertTrue(url.contains("attribution=publish"));
        assertTrue(url.contains("placement_id=pl1"));
    }

    @Test
    void fetchesFollowerHistory() {
        var mock = new MockPostProxyClient(Map.of("data", Map.of("records", List.of(Map.of(
                "date", "2026-09-01", "followers_gained", 3, "followers_total", 120)))), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.followerHistory("p1", null, "2026-09-01", null, null);
        assertEquals(120, result.data().records().get(0).followersTotal());
        assertNull(result.data().records().get(0).followersLost());
        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/api/profiles/p1/follower_history?"));
        assertTrue(url.contains("from=2026-09-01"));
    }

    @Test
    void fetchesParticipantStatus() {
        var mock = new MockPostProxyClient(Map.of("is_verified_user", true, "fetched_at", "x"), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.participantStatus("p1", "igsid 1");
        assertTrue(result.isVerifiedUser());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/p1/participants/igsid%201"));
    }

    @Test
    void resolvesLinkedInMention() {
        var mock = new MockPostProxyClient(Map.of("urn", "urn:li:organization:1", "type", "organization",
                "name", "Acme", "vanity_name", "acme"), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.resolveMention("p1", "https://www.linkedin.com/company/acme");
        assertEquals("urn:li:organization:1", result.urn());
        assertEquals("acme", result.vanityName());
        assertTrue(mock.getRequests().get(0).url().contains("url=https://www.linkedin.com/company/acme"));
    }

    @Test
    void searchesInstagramAudio() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(Map.of("id", "a1"))), 200, null);
        var profiles = new ProfilesResource(mock);

        var result = profiles.instagramAvailableAudio("p1", InstagramAudioType.MUSIC, "summer", null);
        assertEquals("a1", result.data().get(0).get("id"));
        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/api/profiles/p1/instagram/available_audio?"));
        assertTrue(url.contains("audio_type=music"));
        assertTrue(url.contains("search_query=summer"));
    }

    @Test
    void readsAndUpdatesFacebookPage() {
        var mock = new MockPostProxyClient(Map.of("about", "Hi"), 200, null);
        var profiles = new ProfilesResource(mock);

        var page = profiles.facebookPage("p1", "page_1", "about,website", null);
        profiles.updateFacebookPage("p1", "page_1", Map.of("about", "New about"));

        assertEquals("Hi", page.get("about"));
        var read = mock.getRequests().get(0);
        assertTrue(read.url().contains("page_id=page_1"));
        assertTrue(read.url().contains("fields=about,website"));
        var update = mock.getRequests().get(1);
        assertEquals("PATCH", update.method());
        assertTrue(update.url().contains("/profiles/p1/facebook/update_page"));
        assertEquals(Map.of("about", "New about", "page_id", "page_1"), update.body());
    }

    @Test
    void movesProfileToAnotherGroup() {
        var mock = new MockPostProxyClient(Map.of("id", "p1", "inactive_reason", "network_taken"), 200, null);
        var profiles = new ProfilesResource(mock);

        var profile = profiles.assignToGroup("p1", "pg_2");
        assertEquals(ProfileInactiveReason.NETWORK_TAKEN, profile.inactiveReason());

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        assertTrue(req.url().contains("/profiles/p1/assign_to_group"));
        assertEquals(Map.of("target_profile_group_id", "pg_2"), req.body());
    }

    @Test
    void startsAndListsChatBackfills() {
        var mock = new MockPostProxyClient(Map.of("id", "cs1", "status", "pending", "chats_seen", 0,
                "messages_imported", 0), 200, null);
        var profiles = new ProfilesResource(mock);

        var sync = profiles.backfillChats("p1", "2026-01-01T00:00:00Z");
        assertEquals(PostSyncStatus.PENDING, sync.status());

        mock = new MockPostProxyClient(Map.of("total", 0, "page", 1, "per_page", 5, "data", List.of()), 200, null);
        profiles = new ProfilesResource(mock);
        profiles.chatSyncs("p1", null, PostSyncStatus.RUNNING, null, 5, null);
        var listUrl = mock.getRequests().get(0).url();
        assertTrue(listUrl.contains("/profiles/p1/chat_syncs?"));
        assertTrue(listUrl.contains("status=running"));
        assertTrue(listUrl.contains("per_page=5"));
    }

    @Test
    void fetchesSingleChatSync() {
        var mock = new MockPostProxyClient(Map.of("id", "cs1", "status", "completed", "chats_seen", 4,
                "messages_imported", 31), 200, null);
        var profiles = new ProfilesResource(mock);

        var sync = profiles.chatSync("p1", "cs1");
        assertEquals(31, sync.messagesImported());
        assertTrue(mock.getRequests().get(0).url().contains("/profiles/p1/chat_syncs/cs1"));
    }

    @Test
    void backfillChatsSendsFrom() {
        var mock = new MockPostProxyClient(Map.of("id", "cs1"), 200, null);
        new ProfilesResource(mock).backfillChats("p1", "2026-01-01T00:00:00Z", null, "idem");

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/profiles/p1/backfill_chats"));
        assertEquals(Map.of("from", "2026-01-01T00:00:00Z"), req.body());
        assertEquals("idem", req.idempotencyKey());
    }
}
