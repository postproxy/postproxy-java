package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.param.CreateChatParams;
import dev.postproxy.sdk.param.ListChatsParams;
import dev.postproxy.sdk.resource.ChatsResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChatsResourceTest {

    private static final Map<String, Object> MOCK_CHAT = Map.ofEntries(
            Map.entry("id", "chat_xyz789"),
            Map.entry("profile_id", "prof_abc123"),
            Map.entry("platform", "instagram"),
            Map.entry("participant_external_id", "igsid_8675309"),
            Map.entry("participant_username", "jane_doe"),
            Map.entry("participant_name", "Jane Doe"),
            Map.entry("metadata", Map.of("follower_count", 482)),
            Map.entry("last_message_at", "2026-05-31T15:10:00Z"),
            Map.entry("created_at", "2026-04-12T08:00:00Z")
    );

    @Test
    void listsChats() {
        var mock = new MockPostProxyClient(
                Map.of("total", 1, "page", 0, "per_page", 20, "data", List.of(MOCK_CHAT)),
                200, null);
        var chats = new ChatsResource(mock);

        var result = chats.list("prof_abc123");
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals("chat_xyz789", result.data().get(0).id());
        assertEquals(Platform.INSTAGRAM, result.data().get(0).platform());
        assertEquals("jane_doe", result.data().get(0).participantUsername());
        assertEquals(482, ((Number) result.data().get(0).metadata().get("follower_count")).intValue());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/profiles/prof_abc123/chats"));
    }

    @Test
    void listsChatsWithParams() {
        var mock = new MockPostProxyClient(
                Map.of("total", 0, "page", 1, "per_page", 5, "data", List.of()),
                200, null);
        var chats = new ChatsResource(mock);

        chats.list("prof_abc123", ListChatsParams.builder()
                .page(1).perPage(5).before("2026-05-01T00:00:00Z").after("2026-04-01T00:00:00Z").build());

        var req = mock.getRequests().get(0);
        assertTrue(req.url().contains("page=1"));
        assertTrue(req.url().contains("per_page=5"));
        assertTrue(req.url().contains("before=2026-05-01T00:00:00Z"));
        assertTrue(req.url().contains("after=2026-04-01T00:00:00Z"));
    }

    @Test
    void createsChat() {
        var mock = new MockPostProxyClient(MOCK_CHAT, 200, null);
        var chats = new ChatsResource(mock);

        var chat = chats.create("prof_abc123", "igsid_8675309");
        assertEquals("chat_xyz789", chat.id());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/profiles/prof_abc123/chats"));
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) req.body();
        assertEquals("igsid_8675309", body.get("participant_external_id"));
        assertNull(body.get("participant_username"));
    }

    @Test
    void createsChatWithParticipantFields() {
        var mock = new MockPostProxyClient(MOCK_CHAT, 200, null);
        var chats = new ChatsResource(mock);

        chats.create("prof_abc123", CreateChatParams.builder("igsid_8675309")
                .participantUsername("jane_doe").participantName("Jane Doe").build());

        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("jane_doe", body.get("participant_username"));
        assertEquals("Jane Doe", body.get("participant_name"));
    }

    @Test
    void getsChat() {
        var mock = new MockPostProxyClient(MOCK_CHAT, 200, null);
        var chats = new ChatsResource(mock);

        var chat = chats.get("chat_xyz789");
        assertEquals("chat_xyz789", chat.id());
        assertEquals("prof_abc123", chat.profileId());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/chats/chat_xyz789"));
    }

    @Test
    void archivesChat() {
        var archived = new java.util.LinkedHashMap<>(MOCK_CHAT);
        archived.put("archived", true);
        var mock = new MockPostProxyClient(archived, 200, null);
        var chats = new ChatsResource(mock);

        var chat = chats.archive("chat_xyz789");
        assertEquals(Boolean.TRUE, chat.archived());

        var req = mock.getRequests().get(0);
        assertEquals("POST", req.method());
        assertTrue(req.url().contains("/chats/chat_xyz789/archive"));
    }

    @Test
    void unarchivesChat() {
        var unarchived = new java.util.LinkedHashMap<>(MOCK_CHAT);
        unarchived.put("archived", false);
        var mock = new MockPostProxyClient(unarchived, 200, null);
        var chats = new ChatsResource(mock);

        var chat = chats.unarchive("chat_xyz789");
        assertEquals(Boolean.FALSE, chat.archived());

        var req = mock.getRequests().get(0);
        assertEquals("DELETE", req.method());
        assertTrue(req.url().contains("/chats/chat_xyz789/archive"));
    }
}
