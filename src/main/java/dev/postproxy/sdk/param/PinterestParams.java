package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.PinterestFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PinterestParams(
        @JsonProperty("format") PinterestFormat format,
        @JsonProperty("title") String title,
        @JsonProperty("board_id") String boardId,
        @JsonProperty("destination_link") String destinationLink,
        @JsonProperty("cover_url") String coverUrl,
        // Local path to a cover image, uploaded with the post; a base64 data URL
        // ("data:image/jpeg;base64,...") is sent as-is.
        @JsonProperty("cover_file") String coverFile,
        @JsonProperty("thumb_offset") Integer thumbOffset,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PinterestFormat format;
        private String title;
        private String boardId;
        private String destinationLink;
        private String coverUrl;
        private String coverFile;
        private Integer thumbOffset;
        private Object altText;

        public Builder format(PinterestFormat format) { this.format = format; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder boardId(String boardId) { this.boardId = boardId; return this; }
        public Builder destinationLink(String destinationLink) { this.destinationLink = destinationLink; return this; }
        public Builder coverUrl(String coverUrl) { this.coverUrl = coverUrl; return this; }
        public Builder coverFile(String coverFile) { this.coverFile = coverFile; return this; }
        public Builder thumbOffset(Integer thumbOffset) { this.thumbOffset = thumbOffset; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(java.util.List<String> altText) { this.altText = altText; return this; }

        public PinterestParams build() {
            return new PinterestParams(format, title, boardId, destinationLink, coverUrl, coverFile, thumbOffset, altText);
        }
    }
}
