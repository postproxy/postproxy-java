package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.AcceptedResponse;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.ProfileComment;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProfileCommentsResource {

    private final PostProxyClient client;

    public ProfileCommentsResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<ProfileComment> list(String profileId) {
        return list(profileId, null, null, null);
    }

    public PaginatedResponse<ProfileComment> list(String profileId, String placementId, Integer page, Integer perPage) {
        Map<String, String> query = new LinkedHashMap<>();
        if (placementId != null) query.put("placement_id", placementId);
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());

        return client.get("/api/profiles/" + profileId + "/comments", query, new TypeReference<>() {});
    }

    public ProfileComment get(String profileId, String commentId) {
        return client.get("/api/profiles/" + profileId + "/comments/" + commentId, null, new TypeReference<>() {});
    }

    public ProfileComment create(String profileId, String parentId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("parent_id", parentId);
        body.put("text", text);

        return client.post("/api/profiles/" + profileId + "/comments", null, body, new TypeReference<>() {});
    }

    public AcceptedResponse delete(String profileId, String commentId) {
        return client.delete("/api/profiles/" + profileId + "/comments/" + commentId, null, new TypeReference<>() {});
    }
}
