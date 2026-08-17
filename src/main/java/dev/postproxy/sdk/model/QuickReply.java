package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A tappable chip rendered above the participant's composer, gone once tapped.
 *
 * <p>Facebook and Instagram only; up to 13 per send. {@code contentType} is optional on send (only
 * {@code "text"} is accepted) and always present on responses. {@code title} is capped at 20
 * characters and {@code payload} at 1000; both are required.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuickReply(
        @JsonProperty("content_type") String contentType,
        @JsonProperty("title") String title,
        @JsonProperty("payload") String payload
) {
    /** A quick reply without an explicit content type — the API defaults it to "text". */
    public QuickReply(String title, String payload) {
        this(null, title, payload);
    }
}
