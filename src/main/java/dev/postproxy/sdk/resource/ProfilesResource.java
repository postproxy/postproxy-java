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
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("placement_id", placementId);
        body.put("target_profile_group_id", targetProfileGroupId);

        return client.patch("/api/profiles/" + id + "/assign_placement_to_group", query, body, new TypeReference<>() {});
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
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/profiles/" + id + "/ice_breakers", query,
                Map.of("ice_breakers", iceBreakers), new TypeReference<>() {});
    }

    public SuccessResponse deleteIceBreakers(String id) {
        return deleteIceBreakers(id, null);
    }

    public SuccessResponse deleteIceBreakers(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id + "/ice_breakers", query, new TypeReference<>() {});
    }

    public SuccessResponse delete(String id) {
        return delete(id, null);
    }

    public SuccessResponse delete(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id, query, new TypeReference<>() {});
    }
}
