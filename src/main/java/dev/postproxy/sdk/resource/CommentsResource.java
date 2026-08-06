package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.AcceptedResponse;
import dev.postproxy.sdk.model.BulkComment;
import dev.postproxy.sdk.model.Comment;
import dev.postproxy.sdk.model.Message;
import dev.postproxy.sdk.model.PaginatedResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommentsResource {

    private final PostProxyClient client;

    public CommentsResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<Comment> list(String postId, String profileId) {
        return list(postId, profileId, null, null);
    }

    public PaginatedResponse<Comment> list(String postId, String profileId, Integer page, Integer perPage) {
        return list(postId, profileId, page, perPage, null, null);
    }

    /**
     * Lists a post's comments.
     *
     * <p>{@code from} and {@code to} filter on when PostProxy received the
     * comment ({@code created_at}), not the platform's {@code posted_at}. They
     * apply to top-level comments — one in range brings its full replies list
     * with it.
     */
    public PaginatedResponse<Comment> list(String postId, String profileId, Integer page, Integer perPage,
                                           String from, String to) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);

        return client.get("/api/posts/" + postId + "/comments", query, new TypeReference<>() {});
    }

    /**
     * Lists comments across every post in the profile group.
     *
     * <p>Flat: replies come back as their own entries linked by
     * {@code parentExternalId}, so {@code total} counts every comment.
     * {@code profiles} takes profile IDs or network names, mixed. Every filter
     * is optional; unknown or out-of-scope IDs are ignored rather than
     * erroring. Results are ordered newest first by receipt time.
     */
    public PaginatedResponse<BulkComment> listAll() {
        return listAll(null, null, null, null, null, null, null);
    }

    public PaginatedResponse<BulkComment> listAll(List<String> postIds, List<String> profiles, String from, String to,
                                                  Integer page, Integer perPage, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (postIds != null && !postIds.isEmpty()) query.put("post_ids", String.join(",", postIds));
        if (profiles != null && !profiles.isEmpty()) query.put("profiles", String.join(",", profiles));
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/comments", query, new TypeReference<>() {});
    }

    public Comment get(String postId, String commentId, String profileId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        return client.get("/api/posts/" + postId + "/comments/" + commentId, query, new TypeReference<>() {});
    }

    public Comment create(String postId, String profileId, String text) {
        return create(postId, profileId, text, null);
    }

    public Comment create(String postId, String profileId, String text, String parentId) {
        return create(postId, profileId, text, parentId, null);
    }

    public Comment create(String postId, String profileId, String text, String parentId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        if (parentId != null) body.put("parent_id", parentId);

        return client.post("/api/posts/" + postId + "/comments", query, body, new TypeReference<>() {}, idempotencyKey);
    }

    public AcceptedResponse delete(String postId, String commentId, String profileId) {
        return delete(postId, commentId, profileId, null);
    }

    public AcceptedResponse delete(String postId, String commentId, String profileId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        return client.delete("/api/posts/" + postId + "/comments/" + commentId, query, new TypeReference<>() {}, idempotencyKey);
    }

    public AcceptedResponse hide(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "hide", null);
    }

    public AcceptedResponse hide(String postId, String commentId, String profileId, String idempotencyKey) {
        return commentAction(postId, commentId, profileId, "hide", idempotencyKey);
    }

    public AcceptedResponse unhide(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "unhide", null);
    }

    public AcceptedResponse unhide(String postId, String commentId, String profileId, String idempotencyKey) {
        return commentAction(postId, commentId, profileId, "unhide", idempotencyKey);
    }

    public AcceptedResponse like(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "like", null);
    }

    public AcceptedResponse like(String postId, String commentId, String profileId, String idempotencyKey) {
        return commentAction(postId, commentId, profileId, "like", idempotencyKey);
    }

    public AcceptedResponse unlike(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "unlike", null);
    }

    public AcceptedResponse unlike(String postId, String commentId, String profileId, String idempotencyKey) {
        return commentAction(postId, commentId, profileId, "unlike", idempotencyKey);
    }

    private AcceptedResponse commentAction(String postId, String commentId, String profileId, String action,
                                           String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        String path = "/api/posts/" + postId + "/comments/" + commentId + "/" + action;
        return client.post(path, query, null, new TypeReference<>() {}, idempotencyKey);
    }

    public Message privateReply(String postId, String commentId, String profileId, String text) {
        return privateReply(postId, commentId, profileId, text, null);
    }

    public Message privateReply(String postId, String commentId, String profileId, String text,
                                String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);

        String path = "/api/posts/" + postId + "/comments/" + commentId + "/private_reply";
        return client.post(path, query, body, new TypeReference<>() {}, idempotencyKey);
    }
}
