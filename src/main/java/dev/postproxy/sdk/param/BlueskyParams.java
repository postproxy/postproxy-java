package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.BlueskyFormat;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BlueskyParams(
        @JsonProperty("format") BlueskyFormat format,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText
) {
    public BlueskyParams(BlueskyFormat format) {
        this(format, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlueskyFormat format;
        private Object altText;

        public Builder format(BlueskyFormat format) { this.format = format; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(List<String> altText) { this.altText = altText; return this; }

        public BlueskyParams build() {
            return new BlueskyParams(format, altText);
        }
    }
}
