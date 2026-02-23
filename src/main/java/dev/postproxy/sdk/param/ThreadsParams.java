package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.ThreadsFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ThreadsParams(
        @JsonProperty("format") ThreadsFormat format
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ThreadsFormat format;

        public Builder format(ThreadsFormat format) { this.format = format; return this; }

        public ThreadsParams build() {
            return new ThreadsParams(format);
        }
    }
}
