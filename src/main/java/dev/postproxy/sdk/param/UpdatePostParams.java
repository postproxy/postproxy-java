package dev.postproxy.sdk.param;

import java.nio.file.Path;
import java.util.List;

public record UpdatePostParams(
        String body,
        List<String> profiles,
        List<String> media,
        List<Path> mediaFiles,
        PlatformParams platforms,
        List<ThreadChildInput> thread,
        String scheduledAt,
        Boolean draft,
        String queueId,
        String queuePriority,
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
        private List<ThreadChildInput> thread;
        private String scheduledAt;
        private Boolean draft;
        private String queueId;
        private String queuePriority;
        private String profileGroupId;

        public Builder body(String body) { this.body = body; return this; }
        public Builder profiles(List<String> profiles) { this.profiles = profiles; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder platforms(PlatformParams platforms) { this.platforms = platforms; return this; }
        public Builder thread(List<ThreadChildInput> thread) { this.thread = thread; return this; }
        public Builder scheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; return this; }
        public Builder draft(Boolean draft) { this.draft = draft; return this; }
        public Builder queueId(String queueId) { this.queueId = queueId; return this; }
        public Builder queuePriority(String queuePriority) { this.queuePriority = queuePriority; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public UpdatePostParams build() {
            return new UpdatePostParams(body, profiles, media, mediaFiles, platforms, thread, scheduledAt, draft, queueId, queuePriority, profileGroupId);
        }
    }
}
