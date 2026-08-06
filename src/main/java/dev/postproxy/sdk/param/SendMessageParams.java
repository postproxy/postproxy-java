package dev.postproxy.sdk.param;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record SendMessageParams(
        String body,
        List<String> media,
        List<Path> mediaFiles,
        String tag,
        String replyToExternalId,
        Map<String, Object> replyMarkup,
        String profileGroupId,
        /** Optional Idempotency-Key: retrying with the same key replays the original response. */
        @com.fasterxml.jackson.annotation.JsonIgnore String idempotencyKey
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private List<String> media;
        private List<Path> mediaFiles;
        private String tag;
        private String replyToExternalId;
        private Map<String, Object> replyMarkup;
        private String profileGroupId;
        private String idempotencyKey;

        public Builder body(String body) { this.body = body; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder tag(String tag) { this.tag = tag; return this; }
        public Builder replyToExternalId(String replyToExternalId) { this.replyToExternalId = replyToExternalId; return this; }
        public Builder replyMarkup(Map<String, Object> replyMarkup) { this.replyMarkup = replyMarkup; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public SendMessageParams build() {
            return new SendMessageParams(body, media, mediaFiles, tag, replyToExternalId, replyMarkup, profileGroupId, idempotencyKey);
        }
    }
}
