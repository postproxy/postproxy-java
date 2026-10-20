package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.ListResponse;
import dev.postproxy.sdk.model.WhatsAppTemplate;
import dev.postproxy.sdk.param.WhatsAppTemplateInput;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WhatsApp message templates, which live on the WhatsApp Business account (the profile). A
 * template is addressed by its PostProxy id, Meta's id, or its name — pass {@code language} with a
 * name that exists in several languages.
 */
public class WhatsAppTemplatesResource {

    private final PostProxyClient client;

    public WhatsAppTemplatesResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<WhatsAppTemplate> list(String profileId) {
        return list(profileId, null, null, null, null, null);
    }

    /** {@code refresh} re-reads the templates from Meta first. */
    public ListResponse<WhatsAppTemplate> list(String profileId, String name, String language, String status,
                                               Boolean refresh, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (name != null) query.put("name", name);
        if (language != null) query.put("language", language);
        if (status != null) query.put("status", status);
        if (refresh != null) query.put("refresh", refresh.toString());
        putProfileGroup(query, profileGroupId);

        return client.get(path(profileId, null), query, new TypeReference<>() {});
    }

    public WhatsAppTemplate get(String profileId, String id) {
        return get(profileId, id, null, null);
    }

    public WhatsAppTemplate get(String profileId, String id, String language, String profileGroupId) {
        return client.get(path(profileId, id), languageQuery(language, profileGroupId), new TypeReference<>() {});
    }

    /**
     * Submits the template to Meta for review; follow {@code status} via {@link #get(String, String)}
     * or the {@code message_template.status_updated} webhook.
     */
    public WhatsAppTemplate create(String profileId, WhatsAppTemplateInput template) {
        return create(profileId, template, null, null);
    }

    public WhatsAppTemplate create(String profileId, WhatsAppTemplateInput template, String profileGroupId,
                                   String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.post(path(profileId, null), query, template, new TypeReference<>() {}, idempotencyKey);
    }

    public WhatsAppTemplate update(String profileId, String id, List<Map<String, Object>> components,
                                   Integer messageSendTtlSeconds) {
        return update(profileId, id, components, messageSendTtlSeconds, null, null, null);
    }

    public WhatsAppTemplate update(String profileId, String id, List<Map<String, Object>> components,
                                   Integer messageSendTtlSeconds, String language, String profileGroupId,
                                   String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (components != null) body.put("components", components);
        if (messageSendTtlSeconds != null) body.put("message_send_ttl_seconds", messageSendTtlSeconds);

        return client.patch(path(profileId, id), languageQuery(language, profileGroupId), body,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Deleting by name without {@code language} removes every language of the template. */
    public Map<String, Object> delete(String profileId, String id) {
        return delete(profileId, id, null, null, null);
    }

    public Map<String, Object> delete(String profileId, String id, String language, String profileGroupId,
                                      String idempotencyKey) {
        return client.delete(path(profileId, id), languageQuery(language, profileGroupId),
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Meta's ready-made template library. */
    public Map<String, Object> library(String profileId) {
        return library(profileId, null, null, null);
    }

    public Map<String, Object> library(String profileId, String name, String language, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (name != null) query.put("name", name);
        if (language != null) query.put("language", language);
        putProfileGroup(query, profileGroupId);

        return client.get(path(profileId, null) + "/library", query, new TypeReference<>() {});
    }

    private static String path(String profileId, String id) {
        String base = "/api/profiles/" + profileId + "/templates";
        return id == null ? base : base + "/" + URLEncoder.encode(id, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private Map<String, String> languageQuery(String language, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (language != null) query.put("language", language);
        putProfileGroup(query, profileGroupId);
        return query;
    }

    private void putProfileGroup(Map<String, String> query, String profileGroupId) {
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
    }
}
