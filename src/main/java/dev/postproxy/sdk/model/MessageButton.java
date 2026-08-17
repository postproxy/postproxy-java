package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A button attached to the message itself, delivered as a Meta generic template.
 *
 * <p>Facebook and Instagram only; up to 3 per send. {@code url} is required and must be https when
 * {@code type} is {@code "web_url"}; {@code payload} is required when {@code type} is {@code
 * "postback"}. {@code type} is a plain string rather than an enum so a new Meta button type needs no
 * SDK release.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageButton(
        @JsonProperty("type") String type,
        @JsonProperty("title") String title,
        @JsonProperty("url") String url,
        @JsonProperty("payload") String payload
) {
    /** A button that opens a link. The URL must be https. */
    public static MessageButton webUrl(String title, String url) {
        return new MessageButton("web_url", title, url, null);
    }

    /** A button that posts your payload back as an inbound message. */
    public static MessageButton postback(String title, String payload) {
        return new MessageButton("postback", title, null, payload);
    }
}
