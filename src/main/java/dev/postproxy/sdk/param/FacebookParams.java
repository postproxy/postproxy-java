package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.FacebookFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FacebookParams(
        @JsonProperty("format") FacebookFormat format,
        @JsonProperty("title") String title,
        @JsonProperty("first_comment") String firstComment,
        @JsonProperty("page_id") String pageId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FacebookFormat format;
        private String title;
        private String firstComment;
        private String pageId;

        public Builder format(FacebookFormat format) { this.format = format; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder firstComment(String firstComment) { this.firstComment = firstComment; return this; }
        public Builder pageId(String pageId) { this.pageId = pageId; return this; }

        public FacebookParams build() {
            return new FacebookParams(format, title, firstComment, pageId);
        }
    }
}
