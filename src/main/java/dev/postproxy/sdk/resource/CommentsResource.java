package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.AcceptedResponse;
import dev.postproxy.sdk.model.Comment;
import dev.postproxy.sdk.model.PaginatedResponse;

import java.util.LinkedHashMap;
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
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());

        return client.get("/api/posts/" + postId + "/comments", query, new TypeReference<>() {});
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
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        if (parentId != null) body.put("parent_id", parentId);

        return client.post("/api/posts/" + postId + "/comments", query, body, new TypeReference<>() {});
    }

    public AcceptedResponse delete(String postId, String commentId, String profileId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        return client.delete("/api/posts/" + postId + "/comments/" + commentId, query, new TypeReference<>() {});
    }

    public AcceptedResponse hide(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "hide");
    }

    public AcceptedResponse unhide(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "unhide");
    }

    public AcceptedResponse like(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "like");
    }

    public AcceptedResponse unlike(String postId, String commentId, String profileId) {
        return commentAction(postId, commentId, profileId, "unlike");
    }

    private AcceptedResponse commentAction(String postId, String commentId, String profileId, String action) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("profile_id", profileId);

        String path = "/api/posts/" + postId + "/comments/" + commentId + "/" + action;
        return client.post(path, query, null, new TypeReference<>() {});
    }
}
