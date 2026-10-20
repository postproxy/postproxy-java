package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.InstagramFormat;
import dev.postproxy.sdk.model.InstagramTrialStrategy;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstagramParams(
        @JsonProperty("format") InstagramFormat format,
        @JsonProperty("first_comment") String firstComment,
        @JsonProperty("collaborators") List<String> collaborators,
        @JsonProperty("cover_url") String coverUrl,
        // Local path to a cover image, uploaded with the post; a base64 data URL
        // ("data:image/jpeg;base64,...") is sent as-is.
        @JsonProperty("cover_file") String coverFile,
        @JsonProperty("audio_name") String audioName,
        // Music or sound id from profiles().instagramAvailableAudio(...). Profiles
        // connected via Facebook login only.
        @JsonProperty("audio_id") String audioId,
        // 1-100, default 100. Facebook-login profiles only.
        @JsonProperty("audio_volume") Integer audioVolume,
        @JsonProperty("video_volume") Integer videoVolume,
        // Remove the video's own audio track (reel, story).
        @JsonProperty("strip_audio") Boolean stripAudio,
        @JsonProperty("trial_strategy") InstagramTrialStrategy trialStrategy,
        @JsonProperty("thumb_offset") Integer thumbOffset,
        @JsonProperty("user_tags") List<InstagramUserTag> userTags,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText,
        @JsonProperty("made_with_ai") Boolean madeWithAi
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InstagramFormat format;
        private String firstComment;
        private List<String> collaborators;
        private String coverUrl;
        private String coverFile;
        private String audioName;
        private String audioId;
        private Integer audioVolume;
        private Integer videoVolume;
        private Boolean stripAudio;
        private InstagramTrialStrategy trialStrategy;
        private Integer thumbOffset;
        private List<InstagramUserTag> userTags;
        private Object altText;
        private Boolean madeWithAi;

        public Builder format(InstagramFormat format) { this.format = format; return this; }
        public Builder firstComment(String firstComment) { this.firstComment = firstComment; return this; }
        public Builder collaborators(List<String> collaborators) { this.collaborators = collaborators; return this; }
        public Builder coverUrl(String coverUrl) { this.coverUrl = coverUrl; return this; }
        public Builder coverFile(String coverFile) { this.coverFile = coverFile; return this; }
        public Builder audioName(String audioName) { this.audioName = audioName; return this; }
        public Builder audioId(String audioId) { this.audioId = audioId; return this; }
        public Builder audioVolume(Integer audioVolume) { this.audioVolume = audioVolume; return this; }
        public Builder videoVolume(Integer videoVolume) { this.videoVolume = videoVolume; return this; }
        public Builder stripAudio(Boolean stripAudio) { this.stripAudio = stripAudio; return this; }
        public Builder trialStrategy(InstagramTrialStrategy trialStrategy) { this.trialStrategy = trialStrategy; return this; }
        public Builder thumbOffset(Integer thumbOffset) { this.thumbOffset = thumbOffset; return this; }
        public Builder userTags(List<InstagramUserTag> userTags) { this.userTags = userTags; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(List<String> altText) { this.altText = altText; return this; }
        public Builder madeWithAi(Boolean madeWithAi) { this.madeWithAi = madeWithAi; return this; }

        public InstagramParams build() {
            return new InstagramParams(format, firstComment, collaborators, coverUrl, coverFile, audioName, audioId,
                    audioVolume, videoVolume, stripAudio, trialStrategy, thumbOffset, userTags, altText, madeWithAi);
        }
    }
}
