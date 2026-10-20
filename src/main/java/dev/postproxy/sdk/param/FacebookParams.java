package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.FacebookFormat;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FacebookParams(
        @JsonProperty("format") FacebookFormat format,
        @JsonProperty("title") String title,
        @JsonProperty("first_comment") String firstComment,
        @JsonProperty("page_id") String pageId,
        // Link post: Facebook renders the preview card. Text-only, no media.
        @JsonProperty("link") String link,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText,
        // Photo posts only; videos and reels ignore it.
        @JsonProperty("made_with_ai") Boolean madeWithAi,
        // ISO 3166-1 alpha-2 codes; viewers elsewhere won't see the post.
        @JsonProperty("countries") List<String> countries
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FacebookFormat format;
        private String title;
        private String firstComment;
        private String pageId;
        private String link;
        private Object altText;
        private Boolean madeWithAi;
        private List<String> countries;

        public Builder format(FacebookFormat format) { this.format = format; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder firstComment(String firstComment) { this.firstComment = firstComment; return this; }
        public Builder pageId(String pageId) { this.pageId = pageId; return this; }
        public Builder link(String link) { this.link = link; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(List<String> altText) { this.altText = altText; return this; }
        public Builder madeWithAi(Boolean madeWithAi) { this.madeWithAi = madeWithAi; return this; }
        public Builder countries(List<String> countries) { this.countries = countries; return this; }

        public FacebookParams build() {
            return new FacebookParams(format, title, firstComment, pageId, link, altText, madeWithAi, countries);
        }
    }
}
