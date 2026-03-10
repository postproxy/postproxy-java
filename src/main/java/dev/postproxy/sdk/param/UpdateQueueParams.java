package dev.postproxy.sdk.param;

import java.util.List;
import java.util.Map;

public record UpdateQueueParams(
        String name,
        String description,
        String timezone,
        Boolean enabled,
        Integer jitter,
        List<Map<String, Object>> timeslots
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private String timezone;
        private Boolean enabled;
        private Integer jitter;
        private List<Map<String, Object>> timeslots;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder timezone(String timezone) { this.timezone = timezone; return this; }
        public Builder enabled(Boolean enabled) { this.enabled = enabled; return this; }
        public Builder jitter(Integer jitter) { this.jitter = jitter; return this; }
        public Builder timeslots(List<Map<String, Object>> timeslots) { this.timeslots = timeslots; return this; }

        public UpdateQueueParams build() {
            return new UpdateQueueParams(name, description, timezone, enabled, jitter, timeslots);
        }
    }
}
