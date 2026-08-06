package dev.postproxy.sdk.param;

public record DeleteOnPlatformParams(
        String postProfileId,
        String profileId,
        String network,
        String profileGroupId,
        /** Optional Idempotency-Key: retrying with the same key replays the original response. */
        @com.fasterxml.jackson.annotation.JsonIgnore String idempotencyKey
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String postProfileId;
        private String profileId;
        private String network;
        private String profileGroupId;
        private String idempotencyKey;

        public Builder postProfileId(String postProfileId) { this.postProfileId = postProfileId; return this; }
        public Builder profileId(String profileId) { this.profileId = profileId; return this; }
        public Builder network(String network) { this.network = network; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public DeleteOnPlatformParams build() {
            return new DeleteOnPlatformParams(postProfileId, profileId, network, profileGroupId, idempotencyKey);
        }
    }
}
