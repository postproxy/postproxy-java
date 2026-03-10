package dev.postproxy.sdk.param;

import java.util.List;
import java.util.Map;

public record CreateQueueParams(
        String name,
        String profileGroupId,
        String description,
        String timezone,
        Integer jitter,
        List<Map<String, Object>> timeslots
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String profileGroupId;
        private String description;
        private String timezone;
        private Integer jitter;
        private List<Map<String, Object>> timeslots;

        public Builder name(String name) { this.name = name; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder timezone(String timezone) { this.timezone = timezone; return this; }
        public Builder jitter(Integer jitter) { this.jitter = jitter; return this; }
        public Builder timeslots(List<Map<String, Object>> timeslots) { this.timeslots = timeslots; return this; }

        public CreateQueueParams build() {
            if (name == null) throw new IllegalArgumentException("name is required");
            if (profileGroupId == null) throw new IllegalArgumentException("profileGroupId is required");
            return new CreateQueueParams(name, profileGroupId, description, timezone, jitter, timeslots);
        }
    }
}
