package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Queue;
import dev.postproxy.sdk.param.CreateQueueParams;
import dev.postproxy.sdk.param.UpdateQueueParams;
import dev.postproxy.sdk.resource.QueuesResource;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QueuesResourceTest {

    private static final Map<String, Object> MOCK_QUEUE = new LinkedHashMap<>(Map.of(
            "id", "q1abc",
            "name", "Morning Posts",
            "description", "Daily morning content",
            "timezone", "America/New_York",
            "enabled", true,
            "jitter", 10,
            "profile_group_id", "pg123",
            "timeslots", List.of(
                    Map.of("id", 1, "day", 1, "time", "09:00"),
                    Map.of("id", 2, "day", 3, "time", "09:00"),
                    Map.of("id", 3, "day", 5, "time", "14:00")
            ),
            "posts_count", 5
    ));

    @Test
    void listsQueues() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(MOCK_QUEUE)), 200, null);
        var queues = new QueuesResource(mock);

        var result = queues.list();
        assertEquals(1, result.data().size());
        assertEquals("q1abc", result.data().get(0).id());
        assertEquals(3, result.data().get(0).timeslots().size());

        var req = mock.getRequests().get(0);
        assertEquals("GET", req.method());
        assertTrue(req.url().contains("/post_queues"));
    }

    @Test
    void getsQueueById() {
        var mock = new MockPostProxyClient(MOCK_QUEUE, 200, null);
        var queues = new QueuesResource(mock);

        var queue = queues.get("q1abc");
        assertEquals("q1abc", queue.id());
        assertEquals("Morning Posts", queue.name());
        assertTrue(queue.enabled());
        assertEquals(10, queue.jitter());
        assertTrue(mock.getRequests().get(0).url().contains("/post_queues/q1abc"));
    }

    @Test
    void getsNextSlot() {
        var mock = new MockPostProxyClient(Map.of("next_slot", "2026-03-11T14:00:00Z"), 200, null);
        var queues = new QueuesResource(mock);

        var result = queues.nextSlot("q1abc");
        assertEquals("2026-03-11T14:00:00Z", result.nextSlot());
        assertTrue(mock.getRequests().get(0).url().contains("/post_queues/q1abc/next_slot"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsQueue() {
        var mock = new MockPostProxyClient(MOCK_QUEUE, 200, null);
        var queues = new QueuesResource(mock);

        var queue = queues.create(CreateQueueParams.builder()
                .name("Morning Posts")
                .profileGroupId("pg123")
                .timezone("America/New_York")
                .jitter(10)
                .timeslots(List.of(
                        Map.of("day", 1, "time", "09:00"),
                        Map.of("day", 3, "time", "09:00")
                ))
                .build());

        assertEquals("q1abc", queue.id());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("pg123", body.get("profile_group_id"));
        var postQueue = (Map<String, Object>) body.get("post_queue");
        assertEquals("Morning Posts", postQueue.get("name"));
        assertEquals("America/New_York", postQueue.get("timezone"));
        assertEquals(10, postQueue.get("jitter"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void updatesQueue() {
        var updatedQueue = new LinkedHashMap<>(MOCK_QUEUE);
        updatedQueue.put("enabled", false);
        var mock = new MockPostProxyClient(updatedQueue, 200, null);
        var queues = new QueuesResource(mock);

        var queue = queues.update("q1abc", UpdateQueueParams.builder()
                .enabled(false)
                .build());

        assertFalse(queue.enabled());

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        var body = (Map<String, Object>) req.body();
        var postQueue = (Map<String, Object>) body.get("post_queue");
        assertEquals(false, postQueue.get("enabled"));
    }

    @Test
    void deletesQueue() {
        var mock = new MockPostProxyClient(Map.of("deleted", true), 200, null);
        var queues = new QueuesResource(mock);

        var result = queues.delete("q1abc");
        assertTrue(result.deleted());
        assertEquals("DELETE", mock.getRequests().get(0).method());
        assertTrue(mock.getRequests().get(0).url().contains("/post_queues/q1abc"));
    }
}
