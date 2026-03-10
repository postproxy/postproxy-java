package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.DeleteResponse;
import dev.postproxy.sdk.model.ListResponse;
import dev.postproxy.sdk.model.NextSlotResponse;
import dev.postproxy.sdk.model.Queue;
import dev.postproxy.sdk.param.CreateQueueParams;
import dev.postproxy.sdk.param.UpdateQueueParams;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueuesResource {

    private final PostProxyClient client;

    public QueuesResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<Queue> list() {
        return list(null);
    }

    public ListResponse<Queue> list(String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/post_queues", query, new TypeReference<>() {});
    }

    public Queue get(String id) {
        return client.get("/api/post_queues/" + id, null, new TypeReference<>() {});
    }

    public NextSlotResponse nextSlot(String id) {
        return client.get("/api/post_queues/" + id + "/next_slot", null, new TypeReference<>() {});
    }

    public Queue create(CreateQueueParams params) {
        Map<String, Object> postQueue = new LinkedHashMap<>();
        postQueue.put("name", params.name());
        if (params.description() != null) postQueue.put("description", params.description());
        if (params.timezone() != null) postQueue.put("timezone", params.timezone());
        if (params.jitter() != null) postQueue.put("jitter", params.jitter());
        if (params.timeslots() != null) postQueue.put("queue_timeslots_attributes", params.timeslots());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("profile_group_id", params.profileGroupId());
        body.put("post_queue", postQueue);

        return client.post("/api/post_queues", null, body, new TypeReference<>() {});
    }

    public Queue update(String id, UpdateQueueParams params) {
        Map<String, Object> postQueue = new LinkedHashMap<>();
        if (params.name() != null) postQueue.put("name", params.name());
        if (params.description() != null) postQueue.put("description", params.description());
        if (params.timezone() != null) postQueue.put("timezone", params.timezone());
        if (params.enabled() != null) postQueue.put("enabled", params.enabled());
        if (params.jitter() != null) postQueue.put("jitter", params.jitter());
        if (params.timeslots() != null) postQueue.put("queue_timeslots_attributes", params.timeslots());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("post_queue", postQueue);

        return client.patch("/api/post_queues/" + id, null, body, new TypeReference<>() {});
    }

    public DeleteResponse delete(String id) {
        return client.delete("/api/post_queues/" + id, null, new TypeReference<>() {});
    }
}
