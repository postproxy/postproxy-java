package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.*;

import java.util.LinkedHashMap;
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
