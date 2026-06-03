package dev.postproxy.sdk.param;

public record CreateChatParams(
        String participantExternalId,
        String participantUsername,
        String participantName,
        String profileGroupId
) {
    public static Builder builder(String participantExternalId) {
        return new Builder(participantExternalId);
    }

    public static class Builder {
        private final String participantExternalId;
        private String participantUsername;
        private String participantName;
        private String profileGroupId;

        private Builder(String participantExternalId) {
            this.participantExternalId = participantExternalId;
        }

        public Builder participantUsername(String participantUsername) { this.participantUsername = participantUsername; return this; }
        public Builder participantName(String participantName) { this.participantName = participantName; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public CreateChatParams build() {
            return new CreateChatParams(participantExternalId, participantUsername, participantName, profileGroupId);
        }
    }
}
