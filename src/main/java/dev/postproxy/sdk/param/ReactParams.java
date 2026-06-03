package dev.postproxy.sdk.param;

public record ReactParams(
        String reaction,
        String emoji,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reaction;
        private String emoji;
        private String profileGroupId;

        public Builder reaction(String reaction) { this.reaction = reaction; return this; }
        public Builder emoji(String emoji) { this.emoji = emoji; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public ReactParams build() {
            return new ReactParams(reaction, emoji, profileGroupId);
        }
    }
}
