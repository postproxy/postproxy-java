package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * One card of a horizontally scrolling carousel. Facebook and Instagram only; up to 10 per send.
 *
 * <p>{@code title} (80 characters or fewer) plus at least one other field is required; up to 3
 * {@code buttons} per card.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageElement(
        @JsonProperty("title") String title,
        @JsonProperty("subtitle") String subtitle,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("default_action") CardDefaultAction defaultAction,
        @JsonProperty("buttons") List<MessageButton> buttons
) {
    public MessageElement(String title, String subtitle, String imageUrl, List<MessageButton> buttons) {
        this(title, subtitle, imageUrl, null, buttons);
    }
}
