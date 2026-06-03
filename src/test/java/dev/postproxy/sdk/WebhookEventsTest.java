package dev.postproxy.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.postproxy.sdk.webhook.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebhookEventsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String envelope(String type, Object data) {
        try {
            return MAPPER.writeValueAsString(Map.of(
                    "id", "evt_1",
                    "type", type,
                    "created_at", "2026-05-12T00:00:00Z",
                    "data", data
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parsesPostProcessed() {
        var event = WebhookEvents.parse(envelope("post.processed", Map.of(
                "id", "p1", "body", "hi", "status", "processed",
                "created_at", "2026-05-12T00:00:00Z",
                "platforms", List.of(Map.of("id", "pf1", "platform", "twitter", "name", "X"))
        )));
        assertEquals(WebhookEventType.POST_PROCESSED, event.type());
        var data = WebhookEvents.asPostProcessed(event);
        assertEquals("hi", data.body());
        assertEquals(1, data.platforms().size());
    }

    @Test
    void parsesPlatformPostVariants() {
        for (var type : List.of(
                "platform_post.published",
                "platform_post.failed",
                "platform_post.failed_waiting_for_retry",
                "platform_post.insights")) {
            var event = WebhookEvents.parse(envelope(type, Map.of(
                    "id", "pp1", "post_id", "p1", "platform", "twitter",
                    "profile_id", "pf1", "profile_name", "X", "status", "published",
                    "platform_id", "tw_999"
            )));
            var data = WebhookEvents.asPlatformPost(event);
            assertEquals("pp1", data.id());
        }
    }

    @Test
    void parsesProfileStats() {
        var event = WebhookEvents.parse(envelope("profile.stats", Map.of(
                "profile_id", "pf1", "platform", "linkedin",
                "placement_id", "org_1",
                "stats", Map.of("followerCount", 4567),
                "recorded_at", "2026-05-12T00:00:00Z"
        )));
        var data = WebhookEvents.asProfileStats(event);
        assertEquals("org_1", data.placementId());
        assertEquals(4567, ((Number) data.stats().get("followerCount")).intValue());
    }

    @Test
    void parsesCommentCreated() {
        var commentData = new java.util.LinkedHashMap<String, Object>();
        commentData.put("id", "c1");
        commentData.put("post_id", "p1");
        commentData.put("platform_post_id", "pp1");
        commentData.put("platform", "instagram");
        commentData.put("body", "great");
        commentData.put("status", "published");
        commentData.put("author_name", "Jane");
        commentData.put("author_username", "jane");
        commentData.put("like_count", 0);
        commentData.put("reply_count", 0);
        commentData.put("is_hidden", false);
        commentData.put("created_at", "2026-05-12T00:00:00Z");

        var event = WebhookEvents.parse(envelope("comment.created", commentData));
        var data = WebhookEvents.asCommentCreated(event);
        assertEquals("Jane", data.authorName());
    }

    private static Map<String, Object> messageData() {
        var msg = new java.util.LinkedHashMap<String, Object>();
        msg.put("id", "msg_111");
        msg.put("chat_id", "chat_xyz789");
        msg.put("direction", "inbound");
        msg.put("body", "hello");
        msg.put("status", "received");
        msg.put("reactions", List.of());
        msg.put("attachments", List.of());
        msg.put("is_unsupported", false);
        msg.put("created_at", "2026-05-12T00:00:00Z");
        return Map.of("message", msg);
    }

    @Test
    void parsesAllMessageEvents() {
        for (var type : List.of(
                "message.received", "message.sent", "message.delivered", "message.read",
                "message.edited", "message.deleted", "message.failed_waiting_for_retry",
                "message.failed")) {
            var event = WebhookEvents.parse(envelope(type, messageData()));
            assertNotNull(event.type());
            var data = WebhookEvents.asMessageEvent(event);
            assertEquals("msg_111", data.message().id());
            assertEquals("chat_xyz789", data.message().chatId());
        }
    }

    @Test
    void parsesReactionReceived() {
        var msg = new java.util.LinkedHashMap<String, Object>();
        msg.put("id", "msg_111");
        msg.put("chat_id", "chat_xyz789");
        msg.put("direction", "inbound");
        msg.put("body", "hello");
        msg.put("status", "received");
        msg.put("reactions", List.of());
        msg.put("attachments", List.of());
        msg.put("is_unsupported", false);
        msg.put("created_at", "2026-05-12T00:00:00Z");

        var data = new java.util.LinkedHashMap<String, Object>();
        data.put("message", msg);
        data.put("sender_external_id", "psid_123");
        data.put("action", "react");
        data.put("reaction", "love");
        data.put("emoji", "❤️");
        data.put("occurred_at", "2026-06-01T15:02:00Z");

        var event = WebhookEvents.parse(envelope("reaction.received", data));
        assertEquals(WebhookEventType.REACTION_RECEIVED, event.type());
        var parsed = WebhookEvents.asReactionEvent(event);
        assertEquals("react", parsed.action());
        assertEquals("love", parsed.reaction());
        assertEquals("psid_123", parsed.senderExternalId());
        assertEquals("msg_111", parsed.message().id());
    }

    @Test
    void parsesProfileCommentCreated() {
        var data = new java.util.LinkedHashMap<String, Object>();
        data.put("id", "pc1");
        data.put("profile_id", "prof_abc");
        data.put("platform", "google_business");
        data.put("placement_id", "loc_1");
        data.put("body", "Great service");
        data.put("status", "published");
        data.put("author_username", "happy_customer");
        data.put("created_at", "2026-05-12T00:00:00Z");

        var event = WebhookEvents.parse(envelope("profile_comment.created", data));
        assertEquals(WebhookEventType.PROFILE_COMMENT_CREATED, event.type());
        var parsed = WebhookEvents.asProfileCommentCreated(event);
        assertEquals("pc1", parsed.id());
        assertEquals("loc_1", parsed.placementId());
        assertEquals("happy_customer", parsed.authorUsername());
    }

    @Test
    void rejectsUnknownType() {
        assertThrows(WebhookParseException.class, () ->
                WebhookEvents.parse(envelope("foo.bar", Map.of())));
    }

    @Test
    void rejectsInvalidJson() {
        assertThrows(WebhookParseException.class, () ->
                WebhookEvents.parse("not json{"));
    }
}
