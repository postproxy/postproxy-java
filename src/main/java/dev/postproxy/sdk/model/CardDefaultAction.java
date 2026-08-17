package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The tap-through on a {@link MessageCard}. The URL must be https. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CardDefaultAction(
        @JsonProperty("type") String type,
        @JsonProperty("url") String url
) {
    public static CardDefaultAction webUrl(String url) {
        return new CardDefaultAction("web_url", url);
    }
}
