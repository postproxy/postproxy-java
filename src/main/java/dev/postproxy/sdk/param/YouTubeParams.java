package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.YouTubeFormat;
import dev.postproxy.sdk.model.YouTubePrivacy;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record YouTubeParams(
        @JsonProperty("format") YouTubeFormat format,
        @JsonProperty("title") String title,
        @JsonProperty("privacy_status") YouTubePrivacy privacyStatus,
        @JsonProperty("cover_url") String coverUrl,
        @JsonProperty("made_for_kids") Boolean madeForKids
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private YouTubeFormat format;
        private String title;
        private YouTubePrivacy privacyStatus;
        private String coverUrl;
        private Boolean madeForKids;

        public Builder format(YouTubeFormat format) { this.format = format; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder privacyStatus(YouTubePrivacy privacyStatus) { this.privacyStatus = privacyStatus; return this; }
        public Builder coverUrl(String coverUrl) { this.coverUrl = coverUrl; return this; }
        public Builder madeForKids(Boolean madeForKids) { this.madeForKids = madeForKids; return this; }

        public YouTubeParams build() {
            return new YouTubeParams(format, title, privacyStatus, coverUrl, madeForKids);
        }
    }
}
