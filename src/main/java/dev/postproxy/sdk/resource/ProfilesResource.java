package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfilesResource {

    private final PostProxyClient client;

    public ProfilesResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<Profile> list() {
        return list(null);
    }

    public ListResponse<Profile> list(String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles", query, new TypeReference<>() {});
    }

    public Profile get(String id) {
        return get(id, null);
    }

    public Profile get(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id, query, new TypeReference<>() {});
    }

    public ListResponse<Placement> placements(String id) {
        return placements(id, null);
    }

    public ListResponse<Placement> placements(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/placements", query, new TypeReference<>() {});
    }

    /**
     * Fetch the profile stats timeseries. {@code placementId} is required for
     * facebook, linkedin, and telegram profiles.
     */
    public ProfileStatsResponse getProfileStats(String id, String placementId) {
        return getProfileStats(id, placementId, null, null, null);
    }

    public ProfileStatsResponse getProfileStats(String id, String placementId, String from, String to) {
        return getProfileStats(id, placementId, from, to, null);
    }

    public ProfileStatsResponse getProfileStats(String id, String placementId, String from, String to, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (placementId != null) query.put("placement_id", placementId);
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/stats", query, new TypeReference<>() {});
    }

    /**
     * Moves a placement (e.g. a Facebook Page or Telegram channel) to another
     * profile group. {@code placementId} is the placement's external ID as
     * returned by {@link #placements(String)}.
     */
    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId) {
        return assignPlacementToGroup(id, placementId, targetProfileGroupId, null);
    }

    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId, String profileGroupId) {
        return assignPlacementToGroup(id, placementId, targetProfileGroupId, profileGroupId, null);
    }

    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId,
                                                    String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("placement_id", placementId);
        body.put("target_profile_group_id", targetProfileGroupId);

        return client.patch("/api/profiles/" + id + "/assign_placement_to_group", query, body, new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Imports older posts from the platform.
     *
     * <p>Walks the profile's feed backwards from the newest post until it
     * reaches {@code from} or the platform stops returning posts. Runs in the
     * background — poll {@link #postSync(String, String)} with the returned id
     * for progress. Only one backfill runs per profile; starting a second
     * throws {@link dev.postproxy.sdk.exception.ConflictException} carrying the
     * running one's {@code profile_sync_id}.
     */
    public PostSync backfillPosts(String id, String from) {
        return backfillPosts(id, from, null, null);
    }

    public PostSync backfillPosts(String id, String from, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/profiles/" + id + "/backfill_posts", query,
                Map.of("from", from), new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Lists post sync runs for a profile, newest first. Runs are kept for 30 days.
     */
    public PaginatedResponse<PostSync> postSyncs(String id) {
        return postSyncs(id, null, null, null, null, null);
    }

    public PaginatedResponse<PostSync> postSyncs(String id, PostSyncTrigger trigger, PostSyncStatus status,
                                                 Integer page, Integer perPage, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (trigger != null) query.put("trigger", trigger.getValue());
        if (status != null) query.put("status", status.getValue());
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/post_syncs", query, new TypeReference<>() {});
    }

    /**
     * Fetches a single run. Poll this to follow a backfill to completion — the
     * run is finished when its status is COMPLETED or FAILED.
     */
    public PostSync postSync(String id, String postSyncId) {
        return postSync(id, postSyncId, null);
    }

    public PostSync postSync(String id, String postSyncId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/post_syncs/" + postSyncId, query, new TypeReference<>() {});
    }

    /**
     * Lists DM ice breakers. Supported for Instagram profiles only.
     */
    public IceBreakersResponse iceBreakers(String id) {
        return iceBreakers(id, null);
    }

    public IceBreakersResponse iceBreakers(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/ice_breakers", query, new TypeReference<>() {});
    }

    /**
     * Replaces the DM ice breakers for a profile (1-4 items).
     */
    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers) {
        return setIceBreakers(id, iceBreakers, null);
    }

    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers, String profileGroupId) {
        return setIceBreakers(id, iceBreakers, profileGroupId, null);
    }

    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers, String profileGroupId,
                                          String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/profiles/" + id + "/ice_breakers", query,
                Map.of("ice_breakers", iceBreakers), new TypeReference<>() {}, idempotencyKey);
    }

    public SuccessResponse deleteIceBreakers(String id) {
        return deleteIceBreakers(id, null);
    }

    public SuccessResponse deleteIceBreakers(String id, String profileGroupId) {
        return deleteIceBreakers(id, profileGroupId, null);
    }

    public SuccessResponse deleteIceBreakers(String id, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id + "/ice_breakers", query, new TypeReference<>() {}, idempotencyKey);
    }

    public SuccessResponse delete(String id) {
        return delete(id, null);
    }

    public SuccessResponse delete(String id, String profileGroupId) {
        return delete(id, profileGroupId, null);
    }

    public SuccessResponse delete(String id, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id, query, new TypeReference<>() {}, idempotencyKey);
    }
}
