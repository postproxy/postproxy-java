package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.TwitterFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TwitterParams(
        @JsonProperty("format") TwitterFormat format,
        // Required when format is POLL: 2-4 options, max 25 characters each.
        @JsonProperty("poll_options") java.util.List<String> pollOptions,
        // Required when format is POLL: 5 to 10080 minutes (7 days).
        @JsonProperty("poll_duration_minutes") Integer pollDurationMinutes
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TwitterFormat format;
        private java.util.List<String> pollOptions;
        private Integer pollDurationMinutes;

        public Builder format(TwitterFormat format) { this.format = format; return this; }

        public Builder pollOptions(java.util.List<String> pollOptions) { this.pollOptions = pollOptions; return this; }

        public Builder pollDurationMinutes(Integer pollDurationMinutes) { this.pollDurationMinutes = pollDurationMinutes; return this; }

        public TwitterParams build() {
            return new TwitterParams(format, pollOptions, pollDurationMinutes);
        }
    }
}
