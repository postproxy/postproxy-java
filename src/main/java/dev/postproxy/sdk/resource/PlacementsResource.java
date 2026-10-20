package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.AccountPlacement;
import dev.postproxy.sdk.model.PaginatedResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/** Account-wide API keys only — placements span profile groups. */
public class PlacementsResource {

    private final PostProxyClient client;

    public PlacementsResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<AccountPlacement> list() {
        return list(null, null);
    }

    public PaginatedResponse<AccountPlacement> list(Integer page, Integer perPage) {
        Map<String, String> query = new LinkedHashMap<>();
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());

        return client.get("/api/placements", query, new TypeReference<>() {});
    }

    /**
     * When the same placement is reachable through several connected profiles, chooses which one
     * PostProxy posts and reads through.
     */
    public AccountPlacement setActiveProfile(String id, String profileId) {
        return setActiveProfile(id, profileId, null);
    }

    public AccountPlacement setActiveProfile(String id, String profileId, String idempotencyKey) {
        return client.patch("/api/placements/" + id + "/set_active_profile", null,
                Map.of("profile_id", profileId), new TypeReference<>() {}, idempotencyKey);
    }
}
