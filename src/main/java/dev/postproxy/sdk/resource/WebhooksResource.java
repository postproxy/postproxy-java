package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.ListResponse;
import dev.postproxy.sdk.model.PaginatedResponse;
import dev.postproxy.sdk.model.Webhook;
import dev.postproxy.sdk.model.WebhookDelivery;
import dev.postproxy.sdk.param.CreateWebhookParams;
import dev.postproxy.sdk.param.UpdateWebhookParams;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebhooksResource {

    private final PostProxyClient client;

    public WebhooksResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<Webhook> list() {
        return client.get("/api/webhooks", null, new TypeReference<>() {});
    }

    public Webhook get(String id) {
        return client.get("/api/webhooks/" + id, null, new TypeReference<>() {});
    }

    public Webhook create(CreateWebhookParams params) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", params.url());
        body.put("events", params.events());
        if (params.description() != null) body.put("description", params.description());

        return client.post("/api/webhooks", null, body, new TypeReference<>() {});
    }

    public Webhook update(String id, UpdateWebhookParams params) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (params.url() != null) body.put("url", params.url());
        if (params.events() != null) body.put("events", params.events());
        if (params.enabled() != null) body.put("enabled", params.enabled());
        if (params.description() != null) body.put("description", params.description());

        return client.patch("/api/webhooks/" + id, null, body, new TypeReference<>() {});
    }

    public DeleteResponse delete(String id) {
        return client.delete("/api/webhooks/" + id, null, new TypeReference<>() {});
    }

    public PaginatedResponse<WebhookDelivery> deliveries(String id) {
        return deliveries(id, null, null);
    }

    public PaginatedResponse<WebhookDelivery> deliveries(String id, Integer page, Integer perPage) {
        Map<String, String> query = new LinkedHashMap<>();
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());

        return client.get("/api/webhooks/" + id + "/deliveries", query, new TypeReference<>() {});
    }
}
