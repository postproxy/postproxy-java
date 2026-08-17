package dev.postproxy.sdk;

import dev.postproxy.sdk.model.CardDefaultAction;
import dev.postproxy.sdk.model.MessageButton;
import dev.postproxy.sdk.model.MessageCard;
import dev.postproxy.sdk.model.MessageDirection;
import dev.postproxy.sdk.model.MessageStatus;
import dev.postproxy.sdk.model.QuickReply;
import dev.postproxy.sdk.model.TappedAction;
import dev.postproxy.sdk.param.EditMessageParams;
import dev.postproxy.sdk.param.ListMessagesParams;
import dev.postproxy.sdk.param.ReactParams;
import dev.postproxy.sdk.param.SendMessageParams;
import dev.postproxy.sdk.resource.MessagesResource;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessagesResourceTest {

    private static final Map<String, Object> MOCK_MESSAGE = Map.ofEntries(
            Map.entry("id", "msg_111"),
            Map.entry("chat_id", "chat_xyz789"),
            Map.entry("external_id", "mid.abc123"),
            Map.entry("direction", "inbound"),
            Map.entry("body", "Hey, do you ship internationally?"),
            Map.entry("status", "received"),
            Map.entry("reactions", List.of(Map.of(
                    "sender_external_id", "psid_123", "emoji", "❤️", "reaction", "love",
                    "at", "2026-05-31T14:04:00Z"))),
            Map.entry("attachments", List.of(Map.of(
                    "id", "att_zxc987", "type", "image",
                    "url", "https://storage.postproxy.dev/x", "status", "processed",
                    "external_id", "529233764205652"))),
            Map.entry("is_unsupported", false),
            Map.entry("created_at", "2026-05-31T14:02:01Z")
    );

    private static final Map<String, Object> MOCK_OUTBOUND = Map.ofEntries(
            Map.entry("id", "msg_222"),
            Map.entry("chat_id", "chat_xyz789"),
            Map.entry("direction", "outbound"),
            Map.entry("body", "Yes, we ship worldwide!"),
            Map.entry("status", "pending"),
            Map.entry("reactions", List.of()),
            Map.entry("attachments", List.of()),
            Map.entry("is_unsupported", false),
            Map.entry("created_at", "2026-05-31T15:30:05Z")
    );

    @Test
    void listsMessages() {
        var mock = new MockPostProxyClient(
                Map.of("total", 1, "page", 0, "per_page", 20, "data", List.of(MOCK_MESSAGE)),
                200, null);
        var messages = new MessagesResource(mock);

        var result = messages.list("chat_xyz789");
        assertEquals(1, result.total());
        var msg = result.data().get(0);
        assertEquals("msg_111", msg.id());
        assertEquals(MessageDirection.INBOUND, msg.direction());
        assertEquals(MessageStatus.RECEIVED, msg.status());
        assertEquals(1, msg.reactions().size());
        assertEquals("love", msg.reactions().get(0).reaction());
        assertEquals(1, msg.attachments().size());
        assertEquals("image", msg.attachments().get(0).type());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/chats/chat_xyz789/messages"));
    }

    @Test
    void listsMessagesWithFilters() {
        var mock = new MockPostProxyClient(
                Map.of("total", 0, "page", 0, "per_page", 20, "data", List.of()),
                200, null);
        var messages = new MessagesResource(mock);

        messages.list("chat_xyz789", ListMessagesParams.builder()
                .direction(MessageDirection.INBOUND).status(MessageStatus.RECEIVED).build());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("direction=inbound"));
        assertTrue(req.url().contains("status=received"));
    }

    @Test
    void sendsTextMessageAsJson() {
        var mock = new MockPostProxyClient(MOCK_OUTBOUND, 200, null);
        var messages = new MessagesResource(mock);

        var msg = messages.send("chat_xyz789", SendMessageParams.builder()
                .body("Yes, we ship worldwide!").build());
        assertEquals("msg_222", msg.id());
        assertEquals(MessageStatus.PENDING, msg.status());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/chats/chat_xyz789/messages"));
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("Yes, we ship worldwide!", body.get("body"));
        assertNull(body.get("__fileGroups"));
    }

    @Test
    void sendsMediaUrlAsJson() {
        var mock = new MockPostProxyClient(MOCK_OUTBOUND, 200, null);
        var messages = new MessagesResource(mock);

        messages.send("chat_xyz789", SendMessageParams.builder()
                .media(List.of("https://cdn.example.com/photo.png")).build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals(List.of("https://cdn.example.com/photo.png"), body.get("media"));
        assertNull(body.get("__fileGroups"));
    }

    @Test
    void sendsMediaFileAsMultipart() {
        var mock = new MockPostProxyClient(MOCK_OUTBOUND, 200, null);
        var messages = new MessagesResource(mock);

        messages.send("chat_xyz789", SendMessageParams.builder()
                .mediaFiles(List.of(Path.of("photo.png"))).build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertNotNull(body.get("__fileGroups"));
        @SuppressWarnings("unchecked")
        var fileGroups = (Map<String, List<Path>>) body.get("__fileGroups");
        assertTrue(fileGroups.containsKey("media[]"));
    }

    @Test
    void sendsWithTagAndReplyMarkup() {
        var mock = new MockPostProxyClient(MOCK_OUTBOUND, 200, null);
        var messages = new MessagesResource(mock);

        messages.send("chat_xyz789", SendMessageParams.builder()
                .body("Following up")
                .tag("HUMAN_AGENT")
                .replyToExternalId("mid.999")
                .replyMarkup(Map.of("force_reply", true))
                .build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("HUMAN_AGENT", body.get("tag"));
        assertEquals("mid.999", body.get("reply_to_external_id"));
        assertEquals(Map.of("force_reply", true), body.get("reply_markup"));
    }

    @Test
    void sendsQuickReplies() {
        var mock = new MockPostProxyClient(
                mergeOutbound("quick_replies", List.of(Map.of(
                        "content_type", "text", "title", "Track order", "payload", "TRACK"))),
                200, null);
        var messages = new MessagesResource(mock);

        var msg = messages.send("chat_xyz789", SendMessageParams.builder()
                .body("What can I help with?")
                .quickReplies(List.of(
                        new QuickReply("Track order", "TRACK"),
                        new QuickReply("Talk to support", "HELP")))
                .build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        @SuppressWarnings("unchecked")
        var sent = (List<QuickReply>) body.get("quick_replies");
        assertEquals(2, sent.size());
        assertEquals("Track order", sent.get(0).title());
        assertEquals("TRACK", sent.get(0).payload());
        // Unset content_type is dropped by @JsonInclude rather than sent as null.
        assertNull(sent.get(0).contentType());

        assertEquals("text", msg.quickReplies().get(0).contentType());
    }

    @Test
    void sendsButtonsWithCard() {
        var mock = new MockPostProxyClient(
                mergeOutbound("buttons", List.of(Map.of(
                        "type", "web_url", "title", "Track", "url", "https://shop.example.com"))),
                200, null);
        var messages = new MessagesResource(mock);

        var msg = messages.send("chat_xyz789", SendMessageParams.builder()
                .body("Your order shipped")
                .buttons(List.of(
                        MessageButton.webUrl("Track", "https://shop.example.com"),
                        MessageButton.postback("Cancel", "CANCEL:123")))
                .card(new MessageCard("Arriving Friday", null,
                        CardDefaultAction.webUrl("https://shop.example.com")))
                .build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        @SuppressWarnings("unchecked")
        var buttons = (List<MessageButton>) body.get("buttons");
        assertEquals(2, buttons.size());
        assertEquals("web_url", buttons.get(0).type());
        assertEquals("https://shop.example.com", buttons.get(0).url());
        assertNull(buttons.get(0).payload());
        assertEquals("postback", buttons.get(1).type());
        assertEquals("CANCEL:123", buttons.get(1).payload());

        var card = (MessageCard) body.get("card");
        assertEquals("Arriving Friday", card.subtitle());
        assertEquals("https://shop.example.com", card.defaultAction().url());

        assertEquals("https://shop.example.com", msg.buttons().get(0).url());
    }

    @Test
    void readsTappedActionOnInboundTap() {
        var mock = new MockPostProxyClient(
                mergeMessage("tapped_action", Map.of(
                        "kind", "quick_reply", "payload", "TRACK", "title", "Track order")),
                200, null);
        var messages = new MessagesResource(mock);

        var msg = messages.get("msg_333");
        assertNotNull(msg.tappedAction());
        assertEquals(TappedAction.KIND_QUICK_REPLY, msg.tappedAction().kind());
        assertEquals("TRACK", msg.tappedAction().payload());
        assertEquals("Track order", msg.tappedAction().title());
    }

    // Map.ofEntries rejects nulls and Map.of has no copy-with, so build the
    // variant fixtures explicitly.
    private static Map<String, Object> mergeOutbound(String key, Object value) {
        var merged = new java.util.LinkedHashMap<String, Object>(MOCK_OUTBOUND);
        merged.put(key, value);
        return merged;
    }

    private static Map<String, Object> mergeMessage(String key, Object value) {
        var merged = new java.util.LinkedHashMap<String, Object>(MOCK_MESSAGE);
        merged.put(key, value);
        return merged;
    }

    @Test
    void getsMessage() {
        var mock = new MockPostProxyClient(MOCK_MESSAGE, 200, null);
        var messages = new MessagesResource(mock);

        var msg = messages.get("msg_111");
        assertEquals("msg_111", msg.id());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/messages/msg_111"));
    }

    @Test
    void editsMessage() {
        var mock = new MockPostProxyClient(MOCK_OUTBOUND, 200, null);
        var messages = new MessagesResource(mock);

        messages.edit("msg_222", EditMessageParams.builder().body("Updated answer").build());

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        assertTrue(req.url().contains("/messages/msg_222"));
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("Updated answer", body.get("body"));
    }

    @Test
    void reactsToMessage() {
        var mock = new MockPostProxyClient(MOCK_MESSAGE, 200, null);
        var messages = new MessagesResource(mock);

        messages.react("msg_111", ReactParams.builder().reaction("love").emoji("❤️").build());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/messages/msg_111/react"));
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("love", body.get("reaction"));
        assertEquals("❤️", body.get("emoji"));
    }

    @Test
    void reactsWithDefaults() {
        var mock = new MockPostProxyClient(MOCK_MESSAGE, 200, null);
        var messages = new MessagesResource(mock);

        messages.react("msg_111");

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertNull(req.body());
    }

    @Test
    void unreactsMessage() {
        var mock = new MockPostProxyClient(MOCK_MESSAGE, 200, null);
        var messages = new MessagesResource(mock);

        messages.unreact("msg_111");

        var req = mock.getRequests().get(0);
        assertEquals("DELETE", req.method());
        assertTrue(req.url().contains("/messages/msg_111/unreact"));
    }
}
