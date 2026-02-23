package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.Post;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.ListPostsParams;
import dev.postproxy.sdk.param.PlatformParams;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PostsResource {

    private final PostProxyClient client;

    public PostsResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<Post> list() {
        return list(null);
    }

    public PaginatedResponse<Post> list(ListPostsParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = client.getDefaultProfileGroupId();

        if (params != null) {
            if (params.page() != null) query.put("page", params.page().toString());
            if (params.perPage() != null) query.put("per_page", params.perPage().toString());
            if (params.status() != null) query.put("status", params.status().getValue());
            if (params.platforms() != null && !params.platforms().isEmpty()) {
                query.put("platforms", params.platforms().stream()
                        .map(p -> p.getValue())
                        .collect(Collectors.joining(",")));
            }
            if (params.scheduledAfter() != null) query.put("scheduled_at", params.scheduledAfter());
            if (params.profileGroupId() != null) pgId = params.profileGroupId();
        }

        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/posts", query, new TypeReference<>() {});
    }

    public Post get(String id) {
        return get(id, null);
    }

    public Post get(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/posts/" + id, query, new TypeReference<>() {});
    }

    public Post create(CreatePostParams params) {
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        Map<String, String> query = new LinkedHashMap<>();
        if (pgId != null) query.put("profile_group_id", pgId);

        if (params.mediaFiles() != null && !params.mediaFiles().isEmpty()) {
            return createMultipart(params, query);
        }
        return createJson(params, query);
    }

    private Post createJson(CreatePostParams params, Map<String, String> query) {
        Map<String, Object> postMap = new LinkedHashMap<>();
        postMap.put("body", params.body());
        if (params.scheduledAt() != null) postMap.put("scheduled_at", params.scheduledAt());
        if (params.draft() != null) postMap.put("draft", params.draft());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("post", postMap);
        body.put("profiles", params.profiles());
        if (params.media() != null && !params.media().isEmpty()) body.put("media", params.media());
        if (params.platforms() != null) body.put("platforms", params.platforms());

        return client.post("/api/posts", query, body, new TypeReference<>() {});
    }

    private Post createMultipart(CreatePostParams params, Map<String, String> query) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("post[body]", params.body());
        if (params.scheduledAt() != null) fields.put("post[scheduled_at]", params.scheduledAt());
        if (params.draft() != null) fields.put("post[draft]", params.draft().toString());
        fields.put("profiles[]", params.profiles());

        if (params.platforms() != null) {
            addPlatformFields(fields, params.platforms());
        }

        return client.postMultipart("/api/posts", query, fields, params.mediaFiles(), new TypeReference<>() {});
    }

    private void addPlatformFields(Map<String, Object> fields, PlatformParams platforms) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = client.getObjectMapper().convertValue(platforms, Map.class);
            for (Map.Entry<String, Object> platformEntry : map.entrySet()) {
                String platformName = platformEntry.getKey();
                if (platformEntry.getValue() instanceof Map<?, ?> paramMap) {
                    for (Map.Entry<?, ?> paramEntry : paramMap.entrySet()) {
                        String key = "platforms[" + platformName + "][" + paramEntry.getKey() + "]";
                        Object value = paramEntry.getValue();
                        if (value instanceof List<?> list) {
                            fields.put(key + "[]", list);
                        } else {
                            fields.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public Post publishDraft(String id) {
        return publishDraft(id, null);
    }

    public Post publishDraft(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/posts/" + id + "/publish", query, null, new TypeReference<>() {});
    }

    public DeleteResponse delete(String id) {
        return delete(id, null);
    }

    public DeleteResponse delete(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/posts/" + id, query, new TypeReference<>() {});
    }
}
