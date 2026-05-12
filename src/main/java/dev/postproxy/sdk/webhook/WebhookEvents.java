package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parser for incoming webhook bodies. Returns a typed {@link WebhookEvent}
 * with {@code data} held as a JsonNode; decode it into a typed payload via
 * {@code as*} helpers that take the parsed event.
 */
public final class WebhookEvents {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private WebhookEvents() {}

    /** Parse a raw JSON webhook body into a typed envelope. */
    public static WebhookEvent parse(String body) {
        WebhookEvent event;
        try {
            event = MAPPER.readValue(body, WebhookEvent.class);
        } catch (Exception e) {
            throw new WebhookParseException("Failed to parse webhook body: " + e.getMessage(), e);
        }
        if (event == null || event.type() == null) {
            throw new WebhookParseException("Unknown or missing webhook event type");
        }
        return event;
    }

    public static PostProcessedData asPostProcessed(WebhookEvent event) {
        return convert(event.data(), PostProcessedData.class);
    }

    public static PostImportedData asPostImported(WebhookEvent event) {
        return convert(event.data(), PostImportedData.class);
    }

    public static PlatformPostData asPlatformPost(WebhookEvent event) {
        return convert(event.data(), PlatformPostData.class);
    }

    public static ProfileEventData asProfileEvent(WebhookEvent event) {
        return convert(event.data(), ProfileEventData.class);
    }

    public static ProfileStatsData asProfileStats(WebhookEvent event) {
        return convert(event.data(), ProfileStatsData.class);
    }

    public static MediaFailedData asMediaFailed(WebhookEvent event) {
        return convert(event.data(), MediaFailedData.class);
    }

    public static CommentCreatedData asCommentCreated(WebhookEvent event) {
        return convert(event.data(), CommentCreatedData.class);
    }

    private static <T> T convert(JsonNode data, Class<T> klass) {
        if (data == null) return null;
        try {
            return MAPPER.treeToValue(data, klass);
        } catch (Exception e) {
            throw new WebhookParseException("Failed to decode event data: " + e.getMessage(), e);
        }
    }
}
