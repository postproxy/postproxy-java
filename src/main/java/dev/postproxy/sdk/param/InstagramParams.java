package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.InstagramFormat;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstagramParams(
        @JsonProperty("format") InstagramFormat format,
        @JsonProperty("first_comment") String firstComment,
        @JsonProperty("collaborators") List<String> collaborators,
        @JsonProperty("cover_url") String coverUrl,
        @JsonProperty("audio_name") String audioName,
        @JsonProperty("trial_strategy") Boolean trialStrategy,
        @JsonProperty("thumb_offset") Integer thumbOffset
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InstagramFormat format;
        private String firstComment;
        private List<String> collaborators;
        private String coverUrl;
        private String audioName;
        private Boolean trialStrategy;
        private Integer thumbOffset;

        public Builder format(InstagramFormat format) { this.format = format; return this; }
        public Builder firstComment(String firstComment) { this.firstComment = firstComment; return this; }
        public Builder collaborators(List<String> collaborators) { this.collaborators = collaborators; return this; }
        public Builder coverUrl(String coverUrl) { this.coverUrl = coverUrl; return this; }
        public Builder audioName(String audioName) { this.audioName = audioName; return this; }
        public Builder trialStrategy(Boolean trialStrategy) { this.trialStrategy = trialStrategy; return this; }
        public Builder thumbOffset(Integer thumbOffset) { this.thumbOffset = thumbOffset; return this; }

        public InstagramParams build() {
            return new InstagramParams(format, firstComment, collaborators, coverUrl, audioName, trialStrategy, thumbOffset);
        }
    }
}
