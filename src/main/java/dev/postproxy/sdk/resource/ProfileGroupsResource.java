package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProfileGroupsResource {

    private final PostProxyClient client;

    public ProfileGroupsResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<ProfileGroup> list() {
        return client.get("/api/profile_groups", null, new TypeReference<>() {});
    }

    public ProfileGroup get(String id) {
        return client.get("/api/profile_groups/" + id, null, new TypeReference<>() {});
    }

    public ProfileGroup create(String name) {
        Map<String, Object> body = Map.of(
                "profile_group", Map.of("name", name)
        );
        return client.post("/api/profile_groups", null, body, new TypeReference<>() {});
    }

    public DeleteResponse delete(String id) {
        return client.delete("/api/profile_groups/" + id, null, new TypeReference<>() {});
    }

    /**
     * Initialize an OAuth connection. For BlueSky and Telegram use
     * {@link #connectBluesky(String, String, String)} and
     * {@link #connectTelegram(String, String)} respectively.
     */
    public ConnectionResponse initializeConnection(String id, Platform platform, String redirectUrl) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("platform", platform.getValue());
        if (redirectUrl != null) body.put("redirect_url", redirectUrl);

        return client.post("/api/profile_groups/" + id + "/initialize_connection", null, body, new TypeReference<>() {});
    }

    public BlueskyConnectionResponse connectBluesky(String id, String identifier, String appPassword) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("platform", Platform.BLUESKY.getValue());
        body.put("identifier", identifier);
        body.put("app_password", appPassword);

        return client.post("/api/profile_groups/" + id + "/initialize_connection", null, body, new TypeReference<>() {});
    }

    /**
     * Submit a Telegram bot token. Channels populate asynchronously — after
     * this call returns, poll {@link ProfilesResource#placements(String)} until
     * it returns at least one placement.
     */
    public TelegramConnectionResponse connectTelegram(String id, String botToken) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("platform", Platform.TELEGRAM.getValue());
        body.put("bot_token", botToken);

        return client.post("/api/profile_groups/" + id + "/initialize_connection", null, body, new TypeReference<>() {});
    }
}
