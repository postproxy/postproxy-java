package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.AcceptedResponse;
import dev.postproxy.sdk.model.Chat;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.ThreadOwner;
import dev.postproxy.sdk.param.CreateChatParams;
import dev.postproxy.sdk.param.ListChatsParams;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatsResource {

    private final PostProxyClient client;

    public ChatsResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<Chat> list(String profileId) {
        return list(profileId, null);
    }

    public PaginatedResponse<Chat> list(String profileId, ListChatsParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = client.getDefaultProfileGroupId();

        if (params != null) {
            if (params.page() != null) query.put("page", params.page().toString());
            if (params.perPage() != null) query.put("per_page", params.perPage().toString());
            if (params.before() != null) query.put("before", params.before());
            if (params.after() != null) query.put("after", params.after());
            if (params.profileGroupId() != null) pgId = params.profileGroupId();
        }

        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + profileId + "/chats", query, new TypeReference<>() {});
    }

    public Chat create(String profileId, String participantExternalId) {
        return create(profileId, CreateChatParams.builder(participantExternalId).build());
    }

    public Chat create(String profileId, CreateChatParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("participant_external_id", params.participantExternalId());
        if (params.participantUsername() != null) body.put("participant_username", params.participantUsername());
        if (params.participantName() != null) body.put("participant_name", params.participantName());
        if (params.placementId() != null) body.put("placement_id", params.placementId());

        return client.post("/api/profiles/" + profileId + "/chats", query, body, new TypeReference<>() {}, params.idempotencyKey());
    }

    public Chat get(String chatId) {
        return get(chatId, null);
    }

    public Chat get(String chatId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/chats/" + chatId, query, new TypeReference<>() {});
    }

    public Chat archive(String chatId) {
        return archive(chatId, null);
    }

    public Chat archive(String chatId, String profileGroupId) {
        return archive(chatId, profileGroupId, null);
    }

    public Chat archive(String chatId, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/chats/" + chatId + "/archive", query, null, new TypeReference<>() {}, idempotencyKey);
    }

    public Chat unarchive(String chatId) {
        return unarchive(chatId, null);
    }

    public Chat unarchive(String chatId, String profileGroupId) {
        return unarchive(chatId, profileGroupId, null);
    }

    public Chat unarchive(String chatId, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/chats/" + chatId + "/archive", query, new TypeReference<>() {}, idempotencyKey);
    }

    /** Marks the latest inbound message as read (shows the read receipt). */
    public Chat markRead(String chatId) {
        return markRead(chatId, null);
    }

    public Chat markRead(String chatId, String profileGroupId) {
        return markRead(chatId, profileGroupId, null);
    }

    public Chat markRead(String chatId, String profileGroupId, String idempotencyKey) {
        return client.post("/api/chats/" + chatId + "/mark_read", chatQuery(profileGroupId), null,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Shows the typing indicator. Facebook, Instagram and WhatsApp only. */
    public AcceptedResponse startTyping(String chatId) {
        return startTyping(chatId, null);
    }

    public AcceptedResponse startTyping(String chatId, String profileGroupId) {
        return startTyping(chatId, profileGroupId, null);
    }

    public AcceptedResponse startTyping(String chatId, String profileGroupId, String idempotencyKey) {
        return client.post("/api/chats/" + chatId + "/start_typing", chatQuery(profileGroupId), null,
                new TypeReference<>() {}, idempotencyKey);
    }

    public AcceptedResponse stopTyping(String chatId) {
        return stopTyping(chatId, null);
    }

    public AcceptedResponse stopTyping(String chatId, String profileGroupId) {
        return stopTyping(chatId, profileGroupId, null);
    }

    public AcceptedResponse stopTyping(String chatId, String profileGroupId, String idempotencyKey) {
        return client.post("/api/chats/" + chatId + "/stop_typing", chatQuery(profileGroupId), null,
                new TypeReference<>() {}, idempotencyKey);
    }

    // --- Thread control (Facebook & Instagram) ---
    //
    // Meta gives one app at a time control of a conversation when several share a Page or
    // Instagram inbox. These return the chat, whose metadata().get("thread_control") holds the
    // owner Meta reports back. metadata is an optional free-form string passed on to the other app.

    /**
     * Hands the conversation to another Meta app, e.g. Page Inbox {@code 263902037430900} or
     * Instagram Inbox {@code 1217981644879628}.
     */
    public Chat passThreadControl(String chatId, String targetAppId) {
        return passThreadControl(chatId, targetAppId, null);
    }

    public Chat passThreadControl(String chatId, String targetAppId, String metadata) {
        return passThreadControl(chatId, targetAppId, metadata, null, null);
    }

    public Chat passThreadControl(String chatId, String targetAppId, String metadata, String profileGroupId,
                                  String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("target_app_id", targetAppId);
        return threadControl(chatId, "pass_thread_control", body, metadata, profileGroupId, idempotencyKey);
    }

    /**
     * Takes the conversation from whichever app holds it. Needs Conversation Routing; Instagram
     * must be connected via Facebook.
     */
    public Chat takeThreadControl(String chatId) {
        return takeThreadControl(chatId, null);
    }

    public Chat takeThreadControl(String chatId, String metadata) {
        return takeThreadControl(chatId, metadata, null, null);
    }

    public Chat takeThreadControl(String chatId, String metadata, String profileGroupId, String idempotencyKey) {
        return threadControl(chatId, "take_thread_control", new LinkedHashMap<>(), metadata, profileGroupId,
                idempotencyKey);
    }

    /** Gives up control; the conversation goes idle. */
    public Chat releaseThreadControl(String chatId) {
        return releaseThreadControl(chatId, null);
    }

    public Chat releaseThreadControl(String chatId, String metadata) {
        return releaseThreadControl(chatId, metadata, null, null);
    }

    public Chat releaseThreadControl(String chatId, String metadata, String profileGroupId, String idempotencyKey) {
        return threadControl(chatId, "release_thread_control", new LinkedHashMap<>(), metadata, profileGroupId,
                idempotencyKey);
    }

    /**
     * Asks the current owner to pass the conversation. Nothing changes until it does — watch for
     * {@code chat.thread_control_changed}.
     */
    public Chat requestThreadControl(String chatId) {
        return requestThreadControl(chatId, null);
    }

    public Chat requestThreadControl(String chatId, String metadata) {
        return requestThreadControl(chatId, metadata, null, null);
    }

    public Chat requestThreadControl(String chatId, String metadata, String profileGroupId, String idempotencyKey) {
        return threadControl(chatId, "request_thread_control", new LinkedHashMap<>(), metadata, profileGroupId,
                idempotencyKey);
    }

    /** Asks Meta who controls the conversation right now. */
    public ThreadOwner threadOwner(String chatId) {
        return threadOwner(chatId, null);
    }

    public ThreadOwner threadOwner(String chatId, String profileGroupId) {
        return client.get("/api/chats/" + chatId + "/thread_owner", chatQuery(profileGroupId),
                new TypeReference<>() {});
    }

    private Chat threadControl(String chatId, String action, Map<String, Object> body, String metadata,
                               String profileGroupId, String idempotencyKey) {
        if (metadata != null) body.put("metadata", metadata);
        return client.post("/api/chats/" + chatId + "/" + action, chatQuery(profileGroupId), body,
                new TypeReference<>() {}, idempotencyKey);
    }

    private Map<String, String> chatQuery(String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
        return query;
    }
}
