package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.TwitterFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TwitterParams(
        @JsonProperty("format") TwitterFormat format
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TwitterFormat format;

        public Builder format(TwitterFormat format) { this.format = format; return this; }

        public TwitterParams build() {
            return new TwitterParams(format);
        }
    }
}
