package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.Chat;
import dev.postproxy.sdk.model.PaginatedResponse;
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

        return client.post("/api/profiles/" + profileId + "/chats", query, body, new TypeReference<>() {});
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
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/chats/" + chatId + "/archive", query, null, new TypeReference<>() {});
    }

    public Chat unarchive(String chatId) {
        return unarchive(chatId, null);
    }

    public Chat unarchive(String chatId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/chats/" + chatId + "/archive", query, new TypeReference<>() {});
    }
}
