package dev.postproxy.sdk.param;

import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.model.PostStatus;
import java.util.List;

public record ListPostsParams(
        Integer page,
        Integer perPage,
        PostStatus status,
        List<Platform> platforms,
        String scheduledAfter,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page;
        private Integer perPage;
        private PostStatus status;
        private List<Platform> platforms;
        private String scheduledAfter;
        private String profileGroupId;

        public Builder page(Integer page) { this.page = page; return this; }
        public Builder perPage(Integer perPage) { this.perPage = perPage; return this; }
        public Builder status(PostStatus status) { this.status = status; return this; }
        public Builder platforms(List<Platform> platforms) { this.platforms = platforms; return this; }
        public Builder scheduledAfter(String scheduledAfter) { this.scheduledAfter = scheduledAfter; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public ListPostsParams build() {
            return new ListPostsParams(page, perPage, status, platforms, scheduledAfter, profileGroupId);
        }
    }
}
