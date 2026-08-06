package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.Message;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.param.EditMessageParams;
import dev.postproxy.sdk.param.ListMessagesParams;
import dev.postproxy.sdk.param.ReactParams;
import dev.postproxy.sdk.param.SendMessageParams;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessagesResource {

    private final PostProxyClient client;

    public MessagesResource(PostProxyClient client) {
        this.client = client;
    }

    public PaginatedResponse<Message> list(String chatId) {
        return list(chatId, null);
    }

    public PaginatedResponse<Message> list(String chatId, ListMessagesParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = client.getDefaultProfileGroupId();

        if (params != null) {
            if (params.page() != null) query.put("page", params.page().toString());
            if (params.perPage() != null) query.put("per_page", params.perPage().toString());
            if (params.direction() != null) query.put("direction", params.direction().getValue());
            if (params.status() != null) query.put("status", params.status().getValue());
            if (params.profileGroupId() != null) pgId = params.profileGroupId();
        }

        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/chats/" + chatId + "/messages", query, new TypeReference<>() {});
    }

    public Message send(String chatId, SendMessageParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        boolean hasFiles = params.mediaFiles() != null && !params.mediaFiles().isEmpty();
        if (hasFiles) {
            return sendMultipart(chatId, params, query);
        }
        return sendJson(chatId, params, query);
    }

    private Message sendJson(String chatId, SendMessageParams params, Map<String, String> query) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (params.body() != null) body.put("body", params.body());
        if (params.media() != null && !params.media().isEmpty()) body.put("media", params.media());
        if (params.tag() != null) body.put("tag", params.tag());
        if (params.replyToExternalId() != null) body.put("reply_to_external_id", params.replyToExternalId());
        if (params.replyMarkup() != null) body.put("reply_markup", params.replyMarkup());

        return client.post("/api/chats/" + chatId + "/messages", query, body, new TypeReference<>() {}, params.idempotencyKey());
    }

    private Message sendMultipart(String chatId, SendMessageParams params, Map<String, String> query) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (params.body() != null) fields.put("body", params.body());
        if (params.tag() != null) fields.put("tag", params.tag());
        if (params.replyToExternalId() != null) fields.put("reply_to_external_id", params.replyToExternalId());
        if (params.media() != null && !params.media().isEmpty()) {
            fields.put("media[]", params.media());
        }

        Map<String, List<Path>> fileGroups = new LinkedHashMap<>();
        fileGroups.put("media[]", params.mediaFiles());

        return client.postMultipart("/api/chats/" + chatId + "/messages", query, fields, fileGroups, new TypeReference<>() {}, params.idempotencyKey());
    }

    public Message get(String messageId) {
        return get(messageId, null);
    }

    public Message get(String messageId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/messages/" + messageId, query, new TypeReference<>() {});
    }

    public Message edit(String messageId, EditMessageParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = params.profileGroupId() != null ? params.profileGroupId() : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        if (params.body() != null) body.put("body", params.body());
        if (params.replyMarkup() != null) body.put("reply_markup", params.replyMarkup());

        return client.patch("/api/messages/" + messageId, query, body, new TypeReference<>() {}, params.idempotencyKey());
    }

    public Message react(String messageId) {
        return react(messageId, null);
    }

    public Message react(String messageId, ReactParams params) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = params != null && params.profileGroupId() != null
                ? params.profileGroupId()
                : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        if (params != null) {
            if (params.reaction() != null) body.put("reaction", params.reaction());
            if (params.emoji() != null) body.put("emoji", params.emoji());
        }

        return client.post(
                "/api/messages/" + messageId + "/react",
                query,
                body.isEmpty() ? null : body,
                new TypeReference<>() {},
                params != null ? params.idempotencyKey() : null
        );
    }

    public Message unreact(String messageId) {
        return unreact(messageId, null);
    }

    public Message unreact(String messageId, String profileGroupId) {
        return unreact(messageId, profileGroupId, null);
    }

    public Message unreact(String messageId, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/messages/" + messageId + "/unreact", query, new TypeReference<>() {}, idempotencyKey);
    }
}
