package dev.postproxy.sdk.param;

import java.util.List;

public record GetStatsParams(
        List<String> postIds,
        List<String> profiles,
        String from,
        String to
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> postIds;
        private List<String> profiles;
        private String from;
        private String to;

        public Builder postIds(List<String> postIds) { this.postIds = postIds; return this; }
        public Builder profiles(List<String> profiles) { this.profiles = profiles; return this; }
        public Builder from(String from) { this.from = from; return this; }
        public Builder to(String to) { this.to = to; return this; }

        public GetStatsParams build() {
            if (postIds == null || postIds.isEmpty()) {
                throw new IllegalArgumentException("postIds is required and must not be empty");
            }
            return new GetStatsParams(postIds, profiles, from, to);
        }
    }
}
