package dev.postproxy.sdk.param;

import java.util.Map;

public record EditMessageParams(
        String body,
        Map<String, Object> replyMarkup,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private Map<String, Object> replyMarkup;
        private String profileGroupId;

        public Builder body(String body) { this.body = body; return this; }
        public Builder replyMarkup(Map<String, Object> replyMarkup) { this.replyMarkup = replyMarkup; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public EditMessageParams build() {
            return new EditMessageParams(body, replyMarkup, profileGroupId);
        }
    }
}
