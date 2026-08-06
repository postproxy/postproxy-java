package dev.postproxy.sdk.param;

public record ReactParams(
        String reaction,
        String emoji,
        String profileGroupId,
        /** Optional Idempotency-Key: retrying with the same key replays the original response. */
        @com.fasterxml.jackson.annotation.JsonIgnore String idempotencyKey
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reaction;
        private String emoji;
        private String profileGroupId;
        private String idempotencyKey;

        public Builder reaction(String reaction) { this.reaction = reaction; return this; }
        public Builder emoji(String emoji) { this.emoji = emoji; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public ReactParams build() {
            return new ReactParams(reaction, emoji, profileGroupId, idempotencyKey);
        }
    }
}
