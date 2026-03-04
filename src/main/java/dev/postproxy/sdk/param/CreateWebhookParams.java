package dev.postproxy.sdk.param;

import java.util.List;

public record CreateWebhookParams(
        String url,
        List<String> events,
        String description
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private List<String> events;
        private String description;

        public Builder url(String url) { this.url = url; return this; }
        public Builder events(List<String> events) { this.events = events; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public CreateWebhookParams build() {
            if (url == null) throw new IllegalArgumentException("url is required");
            if (events == null || events.isEmpty()) throw new IllegalArgumentException("events is required");
            return new CreateWebhookParams(url, events, description);
        }
    }
}
