package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.ApiKey;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.ListResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/** Manages profile-group-scoped API keys. Requires an account-wide key. */
public class ApiKeysResource {

    private final PostProxyClient client;

    public ApiKeysResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<ApiKey> list() {
        return client.get("/api/api_keys", null, new TypeReference<>() {});
    }

    /** The returned {@code token} is shown only once. */
    public ApiKey create(String name, String profileGroupId) {
        return create(name, profileGroupId, null);
    }

    public ApiKey create(String name, String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("profile_group_id", profileGroupId);

        return client.post("/api/api_keys", null, body, new TypeReference<>() {}, idempotencyKey);
    }

    public DeleteResponse delete(String id) {
        return delete(id, null);
    }

    public DeleteResponse delete(String id, String idempotencyKey) {
        return client.delete("/api/api_keys/" + id, null, new TypeReference<>() {}, idempotencyKey);
    }
}
