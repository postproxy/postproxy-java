package dev.postproxy.sdk.param;

import dev.postproxy.sdk.model.MessageDirection;
import dev.postproxy.sdk.model.MessageStatus;

public record ListMessagesParams(
        Integer page,
        Integer perPage,
        MessageDirection direction,
        MessageStatus status,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page;
        private Integer perPage;
        private MessageDirection direction;
        private MessageStatus status;
        private String profileGroupId;

        public Builder page(Integer page) { this.page = page; return this; }
        public Builder perPage(Integer perPage) { this.perPage = perPage; return this; }
        public Builder direction(MessageDirection direction) { this.direction = direction; return this; }
        public Builder status(MessageStatus status) { this.status = status; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public ListMessagesParams build() {
            return new ListMessagesParams(page, perPage, direction, status, profileGroupId);
        }
    }
}
