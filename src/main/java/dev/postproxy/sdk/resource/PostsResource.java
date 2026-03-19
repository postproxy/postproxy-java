package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.Post;
import dev.postproxy.sdk.model.StatsResponse;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.GetStatsParams;
import dev.postproxy.sdk.param.ListPostsParams;
import dev.postproxy.sdk.param.PlatformParams;
import dev.postproxy.sdk.param.ThreadChildInput;
import dev.postproxy.sdk.param.UpdatePostParams;

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

        boolean hasParentFiles = params.mediaFiles() != null && !params.mediaFiles().isEmpty();
        boolean hasThreadFiles = params.thread() != null && params.thread().stream()
                .anyMatch(t -> t.mediaFiles() != null && !t.mediaFiles().isEmpty());
        if (hasParentFiles || hasThreadFiles) {
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
        if (params.thread() != null && !params.thread().isEmpty()) body.put("thread", params.thread());
        if (params.queueId() != null) body.put("queue_id", params.queueId());
        if (params.queuePriority() != null) body.put("queue_priority", params.queuePriority());

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

        Map<String, List<Path>> fileGroups = new LinkedHashMap<>();
        if (params.mediaFiles() != null && !params.mediaFiles().isEmpty()) {
            fileGroups.put("media[]", params.mediaFiles());
        }

        if (params.thread() != null) {
            for (int i = 0; i < params.thread().size(); i++) {
                ThreadChildInput child = params.thread().get(i);
                fields.put("thread[" + i + "][body]", child.body());
                if (child.media() != null && !child.media().isEmpty()) {
                    fields.put("thread[" + i + "][media][]", child.media());
                }
                if (child.mediaFiles() != null && !child.mediaFiles().isEmpty()) {
                    fileGroups.put("thread[" + i + "][media][]", child.mediaFiles());
                }
            }
        }

        return client.postMultipart("/api/posts", query, fields, fileGroups, new TypeReference<>() {});
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

    public Post update(String id, UpdatePostParams params) {
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        Map<String, String> query = new LinkedHashMap<>();
        if (pgId != null) query.put("profile_group_id", pgId);

        boolean hasParentFiles = params.mediaFiles() != null && !params.mediaFiles().isEmpty();
        boolean hasThreadFiles = params.thread() != null && params.thread().stream()
                .anyMatch(t -> t.mediaFiles() != null && !t.mediaFiles().isEmpty());
        if (hasParentFiles || hasThreadFiles) {
            return updateMultipart(id, params, query);
        }
        return updateJson(id, params, query);
    }

    private Post updateJson(String id, UpdatePostParams params, Map<String, String> query) {
        Map<String, Object> postMap = new LinkedHashMap<>();
        if (params.body() != null) postMap.put("body", params.body());
        if (params.scheduledAt() != null) postMap.put("scheduled_at", params.scheduledAt());
        if (params.draft() != null) postMap.put("draft", params.draft());

        Map<String, Object> body = new LinkedHashMap<>();
        if (!postMap.isEmpty()) body.put("post", postMap);
        if (params.profiles() != null) body.put("profiles", params.profiles());
        if (params.media() != null) body.put("media", params.media());
        if (params.platforms() != null) body.put("platforms", params.platforms());
        if (params.thread() != null && !params.thread().isEmpty()) body.put("thread", params.thread());
        if (params.queueId() != null) body.put("queue_id", params.queueId());
        if (params.queuePriority() != null) body.put("queue_priority", params.queuePriority());

        return client.patch("/api/posts/" + id, query, body, new TypeReference<>() {});
    }

    private Post updateMultipart(String id, UpdatePostParams params, Map<String, String> query) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (params.body() != null) fields.put("post[body]", params.body());
        if (params.scheduledAt() != null) fields.put("post[scheduled_at]", params.scheduledAt());
        if (params.draft() != null) fields.put("post[draft]", params.draft().toString());
        if (params.profiles() != null) fields.put("profiles[]", params.profiles());

        if (params.platforms() != null) {
            addPlatformFields(fields, params.platforms());
        }

        Map<String, List<Path>> fileGroups = new LinkedHashMap<>();
        if (params.mediaFiles() != null && !params.mediaFiles().isEmpty()) {
            fileGroups.put("media[]", params.mediaFiles());
        }

        if (params.thread() != null) {
            for (int i = 0; i < params.thread().size(); i++) {
                ThreadChildInput child = params.thread().get(i);
                fields.put("thread[" + i + "][body]", child.body());
                if (child.media() != null && !child.media().isEmpty()) {
                    fields.put("thread[" + i + "][media][]", child.media());
                }
                if (child.mediaFiles() != null && !child.mediaFiles().isEmpty()) {
                    fileGroups.put("thread[" + i + "][media][]", child.mediaFiles());
                }
            }
        }

        return client.patchMultipart("/api/posts/" + id, query, fields, fileGroups, new TypeReference<>() {});
    }

    public StatsResponse stats(GetStatsParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("post_ids", String.join(",", params.postIds()));
        if (params.profiles() != null && !params.profiles().isEmpty()) {
            query.put("profiles", String.join(",", params.profiles()));
        }
        if (params.from() != null) query.put("from", params.from());
        if (params.to() != null) query.put("to", params.to());

        return client.get("/api/posts/stats", query, new TypeReference<>() {});
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
