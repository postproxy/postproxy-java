package dev.postproxy.sdk;

import dev.postproxy.sdk.model.AcceptedResponse;
import dev.postproxy.sdk.model.Comment;
import dev.postproxy.sdk.resource.CommentsResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommentsResourceTest {

    private static final Map<String, Object> MOCK_REPLY = Map.ofEntries(
            Map.entry("id", "cmt_def456"),
            Map.entry("external_id", "17858893269123457"),
            Map.entry("body", "Thanks!"),
            Map.entry("status", "synced"),
            Map.entry("author_username", "author"),
            Map.entry("like_count", 1),
            Map.entry("is_hidden", false),
            Map.entry("created_at", "2026-03-25T10:05:00Z"),
            Map.entry("replies", List.of())
    );

    private static final Map<String, Object> MOCK_COMMENT = Map.ofEntries(
            Map.entry("id", "cmt_abc123"),
            Map.entry("external_id", "17858893269123456"),
            Map.entry("body", "Great post!"),
            Map.entry("status", "synced"),
            Map.entry("author_username", "someuser"),
            Map.entry("like_count", 3),
            Map.entry("is_hidden", false),
            Map.entry("created_at", "2026-03-25T10:01:00Z"),
            Map.entry("replies", List.of(MOCK_REPLY))
    );

    @Test
    void listsComments() {
        var mock = new MockPostProxyClient(
                Map.of("total", 1, "page", 0, "per_page", 20, "data", List.of(MOCK_COMMENT)),
                200, null);
        var comments = new CommentsResource(mock);

        var result = comments.list("post1", "prof1");
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals("cmt_abc123", result.data().get(0).id());
        assertEquals("Great post!", result.data().get(0).body());
        assertEquals(1, result.data().get(0).replies().size());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/posts/post1/comments"));
        assertTrue(req.url().contains("profile_id=prof1"));
    }

    @Test
    void listsCommentsWithPagination() {
        var mock = new MockPostProxyClient(
                Map.of("total", 42, "page", 2, "per_page", 10, "data", List.of()),
                200, null);
        var comments = new CommentsResource(mock);

        var result = comments.list("post1", "prof1", 2, 10);
        assertEquals(42, result.total());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("page=2"));
        assertTrue(req.url().contains("per_page=10"));
    }

    @Test
    void getsComment() {
        var mock = new MockPostProxyClient(MOCK_COMMENT, 200, null);
        var comments = new CommentsResource(mock);

        var comment = comments.get("post1", "cmt_abc123", "prof1");
        assertEquals("cmt_abc123", comment.id());
        assertEquals("Great post!", comment.body());
        assertEquals(3, comment.likeCount());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("/posts/post1/comments/cmt_abc123"));
    }

    @Test
    void createsComment() {
        var created = Map.ofEntries(
                Map.entry("id", "cmt_new"),
                Map.entry("body", "Nice!"),
                Map.entry("status", "pending"),
                Map.entry("like_count", 0),
                Map.entry("is_hidden", false),
                Map.entry("created_at", "2026-03-25T12:00:00Z"),
                Map.entry("replies", List.of())
        );
        var mock = new MockPostProxyClient(created, 200, null);
        var comments = new CommentsResource(mock);

        var comment = comments.create("post1", "prof1", "Nice!");
        assertEquals("cmt_new", comment.id());
        assertEquals("pending", comment.status());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("Nice!", body.get("text"));
        assertNull(body.get("parent_id"));
    }

    @Test
    void createsReply() {
        var reply = Map.ofEntries(
                Map.entry("id", "cmt_reply"),
                Map.entry("body", "Thanks!"),
                Map.entry("status", "pending"),
                Map.entry("like_count", 0),
                Map.entry("is_hidden", false),
                Map.entry("created_at", "2026-03-25T12:00:00Z"),
                Map.entry("replies", List.of())
        );
        var mock = new MockPostProxyClient(reply, 200, null);
        var comments = new CommentsResource(mock);

        var comment = comments.create("post1", "prof1", "Thanks!", "cmt_abc123");

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("Thanks!", body.get("text"));
        assertEquals("cmt_abc123", body.get("parent_id"));
    }

    @Test
    void deletesComment() {
        var mock = new MockPostProxyClient(Map.of("accepted", true), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.delete("post1", "cmt_abc123", "prof1");
        assertTrue(result.accepted());
        assertEquals("DELETE", mock.getRequests().get(0).method());
    }

    @Test
    void hidesComment() {
        var mock = new MockPostProxyClient(Map.of("accepted", true), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.hide("post1", "cmt_abc123", "prof1");
        assertTrue(result.accepted());
        assertTrue(mock.getRequests().get(0).url().contains("/comments/cmt_abc123/hide"));
    }

    @Test
    void unhidesComment() {
        var mock = new MockPostProxyClient(Map.of("accepted", true), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.unhide("post1", "cmt_abc123", "prof1");
        assertTrue(result.accepted());
        assertTrue(mock.getRequests().get(0).url().contains("/comments/cmt_abc123/unhide"));
    }

    @Test
    void likesComment() {
        var mock = new MockPostProxyClient(Map.of("accepted", true), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.like("post1", "cmt_abc123", "prof1");
        assertTrue(result.accepted());
        assertTrue(mock.getRequests().get(0).url().contains("/comments/cmt_abc123/like"));
    }

    @Test
    void unlikesComment() {
        var mock = new MockPostProxyClient(Map.of("accepted", true), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.unlike("post1", "cmt_abc123", "prof1");
        assertTrue(result.accepted());
        assertTrue(mock.getRequests().get(0).url().contains("/comments/cmt_abc123/unlike"));
    }

    @Test
    void parsesAttachmentsAndMetadata() {
        var comment = Map.ofEntries(
                Map.entry("id", "cmt_media"),
                Map.entry("body", "look at this"),
                Map.entry("status", "synced"),
                Map.entry("like_count", 0),
                Map.entry("is_hidden", false),
                Map.entry("metadata", Map.of("is_verified_user", true, "follower_count", 100)),
                Map.entry("attachments", List.of(Map.of(
                        "id", "att_1", "type", "image",
                        "url", "https://storage.postproxy.dev/x", "status", "processed",
                        "external_id", "529233764205652"))),
                Map.entry("created_at", "2026-03-25T10:01:00Z"),
                Map.entry("replies", List.of())
        );
        var mock = new MockPostProxyClient(comment, 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.get("post1", "cmt_media", "prof1");
        assertEquals(true, result.metadata().get("is_verified_user"));
        assertEquals(1, result.attachments().size());
        assertEquals("image", result.attachments().get(0).type());
        assertEquals("att_1", result.attachments().get(0).id());
    }

    @Test
    void privateReplyReturnsMessage() {
        var message = Map.ofEntries(
                Map.entry("id", "msg_333"),
                Map.entry("chat_id", "chat_xyz789"),
                Map.entry("direction", "outbound"),
                Map.entry("body", "DM-ing you the details."),
                Map.entry("status", "pending"),
                Map.entry("external_comment_id", "17858893269123456"),
                Map.entry("reactions", List.of()),
                Map.entry("attachments", List.of()),
                Map.entry("is_unsupported", false),
                Map.entry("created_at", "2026-05-31T15:35:00Z")
        );
        var mock = new MockPostProxyClient(message, 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.privateReply("post1", "cmt_abc123", "prof1", "DM-ing you the details.");
        assertEquals("msg_333", result.id());
        assertEquals("chat_xyz789", result.chatId());
        assertEquals("17858893269123456", result.externalCommentId());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/posts/post1/comments/cmt_abc123/private_reply"));
        assertTrue(req.url().contains("profile_id=prof1"));
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("DM-ing you the details.", body.get("text"));
    }

    @Test
    void listAcceptsDateFilters() {
        var mock = new MockPostProxyClient(
                Map.of("total", 0, "page", 0, "per_page", 20, "data", List.of()), 200, null);
        var comments = new CommentsResource(mock);

        comments.list("post-1", "prof-1", null, null, "2026-03-25", "2026-03-26T12:00:00Z");

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("from=2026-03-25"));
        assertTrue(url.contains("to=2026-03-26T12:00:00Z"));
    }

    @Test
    void listsCommentsAcrossPosts() {
        Map<String, Object> bulk = Map.ofEntries(
                Map.entry("post_id", "abc123xyz"),
                Map.entry("profile_id", "prof456"),
                Map.entry("platform", "instagram"),
                Map.entry("id", "cmt_abc123"),
                Map.entry("body", "Great post!"),
                Map.entry("like_count", 3),
                Map.entry("created_at", "2026-03-25T10:01:00Z")
        );
        Map<String, Object> reply = Map.ofEntries(
                Map.entry("post_id", "abc123xyz"),
                Map.entry("profile_id", "prof456"),
                Map.entry("platform", "instagram"),
                Map.entry("id", "cmt_def456"),
                Map.entry("body", "Thanks!"),
                Map.entry("parent_external_id", "17858893269123456"),
                Map.entry("like_count", 1),
                Map.entry("created_at", "2026-03-25T10:05:00Z")
        );
        var mock = new MockPostProxyClient(
                Map.of("total", 2, "page", 0, "per_page", 50, "data", List.of(bulk, reply)), 200, null);
        var comments = new CommentsResource(mock);

        var result = comments.listAll(
                List.of("abc123xyz", "def456uvw"), List.of("instagram", "prof456"),
                "2026-03-25", null, null, 50, null);

        assertEquals(2, result.total());
        assertEquals("abc123xyz", result.data().get(0).postId());
        assertEquals("prof456", result.data().get(0).profileId());
        assertEquals("instagram", result.data().get(0).platform().getValue());
        // Flat: the reply is its own entry, linked by parent_external_id.
        assertEquals("17858893269123456", result.data().get(1).parentExternalId());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/api/comments"));
        assertTrue(url.contains("post_ids=abc123xyz,def456uvw"));
        assertTrue(url.contains("profiles=instagram,prof456"));
        assertTrue(url.contains("per_page=50"));
    }

    @Test
    void listsCommentsAcrossPostsWithoutFilters() {
        var mock = new MockPostProxyClient(
                Map.of("total", 0, "page", 0, "per_page", 20, "data", List.of()), 200, null);
        var comments = new CommentsResource(mock);

        comments.listAll();

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("/api/comments"));
        assertFalse(url.contains("profiles="));
        assertFalse(url.contains("post_ids="));
    }

    @Test
    void sendsIdempotencyKeyWhenCreatingAComment() {
        var mock = new MockPostProxyClient(
                Map.of("id", "cmt_abc123", "body", "Nice", "created_at", "2026-03-25T10:01:00Z"), 200, null);
        var comments = new CommentsResource(mock);

        comments.create("post-1", "prof-1", "Nice", null, "key-42");

        assertEquals("key-42", mock.getRequests().get(0).idempotencyKey());
    }
}
