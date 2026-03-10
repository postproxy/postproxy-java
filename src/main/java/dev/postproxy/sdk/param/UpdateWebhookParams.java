package dev.postproxy.sdk.param;

import java.util.List;

public record UpdateWebhookParams(
        String url,
        List<String> events,
        Boolean enabled,
        String description
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private List<String> events;
        private Boolean enabled;
        private String description;

        public Builder url(String url) { this.url = url; return this; }
        public Builder events(List<String> events) { this.events = events; return this; }
        public Builder enabled(Boolean enabled) { this.enabled = enabled; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public UpdateWebhookParams build() {
            return new UpdateWebhookParams(url, events, enabled, description);
        }
    }
}
