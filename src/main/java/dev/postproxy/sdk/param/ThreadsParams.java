package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.ThreadsFormat;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ThreadsParams(
        @JsonProperty("format") ThreadsFormat format,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText,
        // One tag, 50 characters or fewer, without the leading # — no "." or "&".
        @JsonProperty("topic_tag") String topicTag
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ThreadsFormat format;
        private Object altText;
        private String topicTag;

        public Builder format(ThreadsFormat format) { this.format = format; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(List<String> altText) { this.altText = altText; return this; }
        public Builder topicTag(String topicTag) { this.topicTag = topicTag; return this; }

        public ThreadsParams build() {
            return new ThreadsParams(format, altText, topicTag);
        }
    }
}
