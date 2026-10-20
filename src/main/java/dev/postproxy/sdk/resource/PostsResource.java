package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.DeleteOnPlatformResponse;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.Post;
import dev.postproxy.sdk.model.StatsResponse;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.DeleteOnPlatformParams;
import dev.postproxy.sdk.param.GetStatsParams;
import dev.postproxy.sdk.param.ListPostsParams;
import dev.postproxy.sdk.param.MediaInput;
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
        Map<String, String> headers = Boolean.TRUE.equals(params.skipDuplicateCheck())
                ? Map.of("X-Skip-Duplicate-Check", "true")
                : null;
        if (hasParentFiles || hasThreadFiles || hasLocalCoverFile(params.platforms())) {
            return createMultipart(params, query, headers);
        }
        return createJson(params, query, headers);
    }

    private Post createJson(CreatePostParams params, Map<String, String> query, Map<String, String> headers) {
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

        return client.post("/api/posts", query, body, new TypeReference<>() {}, params.idempotencyKey(), headers);
    }

    private Post createMultipart(CreatePostParams params, Map<String, String> query, Map<String, String> headers) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("post[body]", params.body());
        if (params.scheduledAt() != null) fields.put("post[scheduled_at]", params.scheduledAt());
        if (params.draft() != null) fields.put("post[draft]", params.draft().toString());
        if (params.queueId() != null) fields.put("queue_id", params.queueId());
        if (params.queuePriority() != null) fields.put("queue_priority", params.queuePriority());
        fields.put("profiles[]", params.profiles());
        if (params.media() != null && !params.media().isEmpty()) {
            fields.put("media[]", mediaUrls(params.media()));
        }

        Map<String, List<Path>> fileGroups = new LinkedHashMap<>();
        if (params.mediaFiles() != null && !params.mediaFiles().isEmpty()) {
            fileGroups.put("media[]", params.mediaFiles());
        }

        if (params.platforms() != null) {
            addPlatformFields(fields, fileGroups, params.platforms());
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

        return client.postMultipart("/api/posts", query, fields, fileGroups, new TypeReference<>() {},
                params.idempotencyKey(), headers);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> platformMap(PlatformParams platforms) {
        return client.getObjectMapper().convertValue(platforms, Map.class);
    }

    // cover_file is a local path unless it's a base64 data URL, which the API
    // takes as a plain string.
    private static boolean isLocalCoverFile(Object value) {
        return value instanceof String s && !s.startsWith("data:");
    }

    private boolean hasLocalCoverFile(PlatformParams platforms) {
        if (platforms == null) return false;
        for (Object params : platformMap(platforms).values()) {
            if (params instanceof Map<?, ?> paramMap && isLocalCoverFile(paramMap.get("cover_file"))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> mediaUrls(List<Object> media) {
        return media.stream().map(MediaInput::urlOf).collect(Collectors.toList());
    }

    // Platform params as flat form fields; a local cover_file is attached as a file.
    private void addPlatformFields(Map<String, Object> fields, Map<String, List<Path>> fileGroups,
                                   PlatformParams platforms) {
        for (Map.Entry<String, Object> platformEntry : platformMap(platforms).entrySet()) {
            String platformName = platformEntry.getKey();
            if (platformEntry.getValue() instanceof Map<?, ?> paramMap) {
                for (Map.Entry<?, ?> paramEntry : paramMap.entrySet()) {
                    String key = "platforms[" + platformName + "][" + paramEntry.getKey() + "]";
                    Object value = paramEntry.getValue();
                    if ("cover_file".equals(paramEntry.getKey()) && isLocalCoverFile(value)) {
                        fileGroups.put(key, List.of(Path.of((String) value)));
                    } else if (value instanceof List<?> list) {
                        fields.put(key + "[]", list);
                    } else {
                        fields.put(key, value);
                    }
                }
            }
        }
    }

    /**
     * Updates a post. On a published post only {@code body}, the YouTube {@code title} and the
     * Pinterest {@code title} / {@code destinationLink} can change; the edit is pushed to the
     * platforms in the background ({@code platform_post.edited} / {@code platform_post.edit_failed}
     * webhooks).
     */
    public Post update(String id, UpdatePostParams params) {
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        Map<String, String> query = new LinkedHashMap<>();
        if (pgId != null) query.put("profile_group_id", pgId);

        boolean hasParentFiles = params.mediaFiles() != null && !params.mediaFiles().isEmpty();
        boolean hasThreadFiles = params.thread() != null && params.thread().stream()
                .anyMatch(t -> t.mediaFiles() != null && !t.mediaFiles().isEmpty());
        if (hasParentFiles || hasThreadFiles || hasLocalCoverFile(params.platforms())) {
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

        return client.patch("/api/posts/" + id, query, body, new TypeReference<>() {}, params.idempotencyKey());
    }

    private Post updateMultipart(String id, UpdatePostParams params, Map<String, String> query) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (params.body() != null) fields.put("post[body]", params.body());
        if (params.scheduledAt() != null) fields.put("post[scheduled_at]", params.scheduledAt());
        if (params.draft() != null) fields.put("post[draft]", params.draft().toString());
        if (params.queueId() != null) fields.put("queue_id", params.queueId());
        if (params.queuePriority() != null) fields.put("queue_priority", params.queuePriority());
        if (params.profiles() != null) fields.put("profiles[]", params.profiles());
        if (params.media() != null && !params.media().isEmpty()) {
            fields.put("media[]", mediaUrls(params.media()));
        }

        Map<String, List<Path>> fileGroups = new LinkedHashMap<>();
        if (params.mediaFiles() != null && !params.mediaFiles().isEmpty()) {
            fileGroups.put("media[]", params.mediaFiles());
        }

        if (params.platforms() != null) {
            addPlatformFields(fields, fileGroups, params.platforms());
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

        return client.patchMultipart("/api/posts/" + id, query, fields, fileGroups, new TypeReference<>() {}, params.idempotencyKey());
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
        return publishDraft(id, profileGroupId, null);
    }

    public Post publishDraft(String id, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/posts/" + id + "/publish", query, null, new TypeReference<>() {}, idempotencyKey);
    }

    public DeleteResponse delete(String id) {
        return delete(id, null, null);
    }

    public DeleteResponse delete(String id, String profileGroupId) {
        return delete(id, null, profileGroupId);
    }

    public DeleteResponse delete(String id, Boolean deleteOnPlatform, String profileGroupId) {
        return delete(id, deleteOnPlatform, profileGroupId, null);
    }

    public DeleteResponse delete(String id, Boolean deleteOnPlatform, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
        if (deleteOnPlatform != null) query.put("delete_on_platform", deleteOnPlatform.toString());

        return client.delete("/api/posts/" + id, query, new TypeReference<>() {}, idempotencyKey);
    }

    public DeleteOnPlatformResponse deleteOnPlatform(String id) {
        return deleteOnPlatform(id, null);
    }

    public DeleteOnPlatformResponse deleteOnPlatform(String id, DeleteOnPlatformParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = params != null && params.profileGroupId() != null
                ? params.profileGroupId()
                : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        if (params != null) {
            if (params.postProfileId() != null) body.put("post_profile_id", params.postProfileId());
            if (params.profileId() != null) body.put("profile_id", params.profileId());
            if (params.network() != null) body.put("network", params.network());
        }

        return client.post(
                "/api/posts/" + id + "/delete_on_platform",
                query,
                body.isEmpty() ? null : body,
                new TypeReference<>() {},
                params != null ? params.idempotencyKey() : null
        );
    }
}
