package dev.postproxy.sdk.param;

import java.nio.file.Path;
import java.util.List;

public record CreatePostParams(
        String body,
        List<String> profiles,
        List<String> media,
        List<Path> mediaFiles,
        PlatformParams platforms,
        String scheduledAt,
        Boolean draft,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private List<String> profiles;
        private List<String> media;
        private List<Path> mediaFiles;
        private PlatformParams platforms;
        private String scheduledAt;
        private Boolean draft;
        private String profileGroupId;

        public Builder body(String body) { this.body = body; return this; }
        public Builder profiles(List<String> profiles) { this.profiles = profiles; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder platforms(PlatformParams platforms) { this.platforms = platforms; return this; }
        public Builder scheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; return this; }
        public Builder draft(Boolean draft) { this.draft = draft; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public CreatePostParams build() {
            if (body == null) throw new IllegalArgumentException("body is required");
            if (profiles == null || profiles.isEmpty()) throw new IllegalArgumentException("profiles is required");
            return new CreatePostParams(body, profiles, media, mediaFiles, platforms, scheduledAt, draft, profileGroupId);
        }
    }
}
